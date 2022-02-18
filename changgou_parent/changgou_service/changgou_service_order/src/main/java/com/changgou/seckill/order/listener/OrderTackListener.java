package com.changgou.seckill.order.listener;

import com.changgou.seckill.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class OrderTackListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "order_tack")
    public void autoTack(String message){
        System.out.println("收到自动确认收货消息");
        //自动确认收货
        orderService.autoTack(message);
        }
}