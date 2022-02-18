package com.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.config.ConfirmMessageSender;
import com.changgou.seckill.config.RabbitMQConfig;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.entity.IdWorker;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ConfirmMessageSender confirmMessageSender;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    public static final String SECKILL_GOODS_KEY="seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY="seckill_goods_stock_count_";




    @Override
    public boolean add(Long id, String time, String username) {
        /**
         * 1.获取redis中的商品信息和库存信息，进行判断
         * 2.执行redis的与扣减库存操作，并获取扣减后的库存值
         * 3.如果扣减后的库存值西《=0，则删除redis中相应的商品信息与库存信息
         * 4.基于MQ完成MySQL的数据同步，进行异步下单并扣减库存
         */

        //        防止用户恶意刷单

        String result = this.preventRepeatCommit(username, id);
        if ("fail".equals(result)){
            return false;
        }

        //防止相同商品重复购买:在根据username和id查询秒杀订单的数据库，如果有记录就返回FALSE
        SeckillOrder order = seckillOrderMapper.getOrderInfoByUserNameAndGoodsId(username, id);
        if (order!=null){
            return false;
        }

        //获取商品的信息
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS_KEY + time).get(id);
        String redisStock = (String) redisTemplate.opsForValue().get(SECKILL_GOODS_STOCK_COUNT_KEY + id);

        if (StringUtils.isEmpty(redisStock)){
            return false;
        }
        int stock = Integer.parseInt(redisStock);
        if (seckillGoods==null|| stock==0){
            return false;
        }
        //执行redids的预扣减库存并获取到扣减之后的库存
        //decrement:减 increment：加   ----》lua脚本语言
        Long decrement = redisTemplate.opsForValue().decrement(SECKILL_GOODS_STOCK_COUNT_KEY + id);
        if (decrement==0){
            //扣减完库存后当前商品没有库存了，应该删除redis中的商品信息和库存信息
            redisTemplate.boundHashOps(SECKILL_GOODS_KEY + time).delete(id);
            redisTemplate.delete(SECKILL_GOODS_STOCK_COUNT_KEY + id);
        }

        //发送消息，保证消息生产者对于消息的不丢失的实现
        //RabbitMQ消息默认保存在内存中，可以考虑持久化（即写入磁盘中  开启RabbitMQ的持久化：交换机持久化，队列持久化，消息持久化）

        //消息体：秒杀的订单
        SeckillOrder seckillOrder=new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");

        //发送消息
        confirmMessageSender.sendMessage("", RabbitMQConfig.SECKILL_ORDER_QUEUE, JSON.toJSONString(seckillOrder));

        return true;
    }
    private String preventRepeatCommit(String username,Long id){
        String redis_key="seckill_user_"+username+"_id_"+id;
        long count = redisTemplate.opsForValue().increment(redis_key, 1);
        if (count==1){
            //当前用户第一次访问
            //对当前的key设置5分钟有效期
            redisTemplate.expire(redis_key,5, TimeUnit.MINUTES);
            return "success";
        }
        if (count>1){
            return "fail";
        }
        return "fail";
    }
}
