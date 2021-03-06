package com.changgou.seckill.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.order.config.RabbitMQConfig;
import com.changgou.seckill.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Ykj
 * @ClassName OrderPayListener
 * @Discription
 * @date 2021/12/19 19:36
 */

@Component
public class OrderPayListener {
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAY)
    public void receivePayMessage(String message){
        System.out.println("接收到了订单支付的消息");

        Map map = JSON.parseObject(message, Map.class);

        //调用业务层，完成订单数据库的修改
        orderService.updatePayStatus((String)map.get("orderId"),(String)map.get("transactionId"));
    }
}
