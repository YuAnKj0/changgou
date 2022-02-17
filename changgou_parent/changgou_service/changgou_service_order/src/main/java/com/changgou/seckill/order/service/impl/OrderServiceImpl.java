package com.changgou.seckill.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.changgou.seckill.entity.IdWorker;
import com.changgou.seckill.goods.feign.SkuFeign;
import com.changgou.seckill.order.config.RabbitMQConfig;
import com.changgou.order.dao.*;
import com.changgou.order.pojo.*;
import com.changgou.seckill.order.dao.*;
import com.changgou.seckill.order.service.CartService;
import com.changgou.seckill.order.service.OrderService;
import com.changgou.seckill.pay.feign.PayFeign;
import com.changgou.seckill.order.pojo.*;
import com.changgou.seckill.user.feign.UserFeign;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }

    @Autowired
    private CartService cartService;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 增加
     * @param order
     */
    @Override
    @GlobalTransactional(name = "order_add")
    public String add(Order order){
        //1.获取购物车的相关数据(redis)
        Map cartMap = cartService.list(order.getUsername());
        List<OrderItem> orderItemList = (List<OrderItem>) cartMap.get("orderItemList");
        //2.统计计算（总金额，总商品数量）
        //3.填充订单数据并保存到tb_order
        order.setTotalNum((Integer) cartMap.get("totalNum"));
        order.setTotalMoney((Integer) cartMap.get("totalMoney"));
        order.setPayMoney((Integer) cartMap.get("totalMoney"));
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setBuyerRate("0");//0：未评价  1：已评价
        order.setSourceType("1");//1:web
        order.setOrderStatus("0"); //0:当前订单未完成 1：订单已完成 2：已退货 3：
        order.setPayStatus("0"); //0：未支付 1：已支付
        order.setConsignStatus("0");//发货状态  0：未发货 1：已发货
        String orderId = idWorker.nextId() + "";
        order.setId(orderId);

        orderMapper.insertSelective(order);
        //4.填充订单项的数据并保存到tb_order_item
        for (OrderItem orderItem : orderItemList) {
            orderItem.setId(idWorker.nextId()+"");
            orderItem.setIsReturn("0");
            orderItem.setOrderId(orderId);
            orderItemMapper.insertSelective(orderItem);
        }
        //扣减库存增加销量
        skuFeign.decrCount(order.getUsername());
        //增加用户积分
        userFeign.addPoints(10);

        //int i=1/0;
        Task task=new Task();
        task.setUpdateTime(new Date());
        task.setCreateTime(new Date());
        task.setMqExchange(RabbitMQConfig.EX_BUYING_ADDPOINTUSER);
        task.setMqRoutingkey(RabbitMQConfig.CG_BUYING_ADDPOINT_KEY);
        Map map=new HashMap();
        map.put("username",order.getUsername());
        map.put("point",order.getPayMoney());
        map.put("orderId",orderId);
        task.setRequestBody(JSON.toJSONString(map));
        taskMapper.insertSelective(task);
        //5.删除购物车的数据(redis)
        redisTemplate.delete("cart_"+order.getUsername());

        //发送延迟消息
        rabbitTemplate.convertAndSend("","queue.ordercreate",orderId);


        return orderId;
    }

    /**
     * 修改
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Order>)orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Order>)orderMapper.selectByExample(example);
    }

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Override
    public void updatePayStatus(String orderId, String transactionId) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order!=null&&"0".equals(order.getPayStatus())){
            //修改订单的支付状态
            order.setPayStatus("1");
            order.setOrderStatus("1");
            order.setUpdateTime(new Date());
            order.setPayTime(new Date());
            order.setTransactionId(transactionId);//微信返回的交易流水号

            orderMapper.updateByPrimaryKey(order);
            //记录订单的日志
            OrderLog orderLog=new OrderLog();
            orderLog.setId(idWorker.nextId());
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("交易流水号："+transactionId);
            orderLog.setOrderId(orderId);
            orderLogMapper.insert(orderLog);
        }



    }

    @Autowired
    private PayFeign payFeign;
    /**
     * 关闭订单
     * @param orderId
     */
    @Override
    @Transactional
    public void closeOrder(String orderId) {
        /**
         * 1.根据订单id查询订单是否存在判断订单的支付状态
         * 2。基于微信查询订单信息（微信）
         * 2.1）如果当前订单的状态为已支付，则进行数据补偿
         * 2.2）如果当前订单为未支付，则修改MySQL中的订单信息，新增订单日志，恢复商品库存，基于微信关闭订单
         */
        System.out.println("关闭订单的业务开启："+orderId);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"0".equals(order.getPayStatus())){
            System.out.println("当前订单不需要关闭");
            return;
        }
        System.out.println("关闭订单校验通过");

        //基于微信查询订单的信息
        Map wxQueryMap=(Map) payFeign.queryOeder(orderId).getData();
        System.out.println("查询微信支付的订单"+wxQueryMap);

        //如果订单的状态为已支付，进行补偿（MySQL）
        if ("SUCCESS".equals(wxQueryMap.get("trade_state"))){
            this.updatePayStatus(orderId,(String) wxQueryMap.get("transaction_id"));
            System.out.println("完成数据补偿");

        }
        //如果订单的支付状态为未支付
        if ("NOTPAY".equals(wxQueryMap.get("trade_state"))){
            System.out.println("执行关闭");
            order.setOrderStatus("4");//订单已关闭
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            //新增日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId()+"");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("4");
            orderLog.setOrderId(order.getId());
            orderLogMapper.insert(orderLog);

            //恢复商品的库存
           // skuFeign.resumeStockNum();
            OrderItem _orderItem=new OrderItem();
            _orderItem.setOrderId(orderId);
            List<OrderItem> orderItemList = orderItemMapper.select(_orderItem);
            for (OrderItem orderItem : orderItemList) {
                skuFeign.resumeStockNum(orderItem.getSkuId(),orderItem.getNum());
            }

            //基于微信关闭订单
            payFeign.closeOrder(orderId);

        }

    }

    /**
     *
     * @param orders
     */

    @Override
    @Transactional
    public void batchSend(List<Order> orders) {
        //判断每一个订单的运单号和物流公司的值是否存在
        for (Order order : orders) {
            if (order.getId()==null) {
                throw new RuntimeException("订单号不存在");
            }
            if (order.getShippingCode()==null||order.getShippingName()==null){
                throw new RuntimeException("请输入运单号或者物流公司名称");
            }
        }
        //进行订单状态的校验
        for (Order order : orders) {
            Order order1=orderMapper.selectByPrimaryKey(order.getId());
            if (!"0".equals(order1.getConsignStatus())||"1".equals(order1.getOrderStatus())){
                throw new RuntimeException("订单状态不合法");
            }
        }
        //修改订单的额状态为已发货
        for (Order order : orders) {
            order.setOrderStatus("2");//已发货
            order.setConsignStatus("1");//已发货
            order.setConsignTime(new Date());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKey(order);

            //记录订单日志
            OrderLog orderLog=new OrderLog();
            orderLog.setId(idWorker.nextId()+"");
            orderLog.setOperateTime(new Date());
            orderLog.setOperater("admin");
            orderLog.setOrderStatus("2");
            orderLog.setConsignStatus("1");
            orderLog.setOrderId(order.getId());
            orderLogMapper.insertSelective(orderLog);
        }

        //写三个循环是防止事务回滚
    }

    /**
     * 确认收货实现
     * @param orderId
     * @param operator
     */
    @Override
    public void take(String orderId, String operator) {
        Order order=orderMapper.selectByPrimaryKey(orderId);
        if (order==null){
            throw new RuntimeException("订单不存在");
        }
        if ("1".equals(order.getConsignStatus())){
            throw new RuntimeException("订单未发货");
        }
        order.setConsignStatus("2");//已送达
        order.setOrderStatus("3");//已完成
        order.setUpdateTime(new Date());
        order.setEndTime(new Date());//交易结束
        orderMapper.updateByPrimaryKey(order);
        //记录日志
        OrderLog orderLog=new OrderLog();
        orderLog.setId(idWorker.nextId()+"");
        orderLog.setOrderId(order.getId());
        orderLog.setOrderStatus("3");
        orderLog.setOperater(operator);
        orderLog.setOperateTime(new Date());
        orderLogMapper.insertSelective(orderLog);
    }

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Override
    public void autoTack(String message) {
        //实现思路：
        //1）从订单配置表中获取订单自动确认期限
        OrderConfig orderConfig=orderConfigMapper.selectByPrimaryKey(1);

        //2）得到当前日期向前数（订单自动确认期限）天。作为过期时间节点
        LocalDate now= LocalDate.now();//获得当前时间
        //获取过期时间节点，在这个日期发货的未收货的订单都
        LocalDate date=now.plusDays(-orderConfig.getOrderTimeout());
        System.out.println(date);

        //3）从订单表中获取过期订单（发货时间小于过期时间，且为未确认收货状态）
        Example example=new Example(Order.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andLessThan("consignTime",date);
        criteria.andEqualTo("orderStatus",2);

        List<Order> orders = orderMapper.selectByExample(example);
        for (Order order : orders) {
            System.out.println("过期订单："+order.getId()+"  "+order.getConsignStatus());
            take(order.getId(),"system");
        }
        //4）循环批量处理，执行确认收货
    }


    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andEqualTo("payType",searchMap.get("payType"));
           	}
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
           	}
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
           	}
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
           	}
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
           	}
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
           	}
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
           	}
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
           	}
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
           	}
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andEqualTo("sourceType",searchMap.get("sourceType"));
           	}
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
           	}
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andEqualTo("orderStatus",searchMap.get("orderStatus"));
           	}
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andEqualTo("payStatus",searchMap.get("payStatus"));
           	}
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andEqualTo("consignStatus",searchMap.get("consignStatus"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }

        }
        return example;





    }

}
