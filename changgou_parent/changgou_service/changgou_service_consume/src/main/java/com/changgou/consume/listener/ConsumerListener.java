package com.changgou.consume.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.consume.config.RabbitMQConfig;
import com.changgou.consume.service.SeckillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    public void receiveSeckillOrderMessage(Message message, Channel channel){

        //设置预抓取总数
        try {
            channel.basicQos(300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //1.转化消息格式
        SeckillOrder seckillOrder= JSON.parseObject(message.getBody(), SeckillOrder.class);
        //基于业务层完成同步MySQL的操作
        int result = seckillOrderService.createOrder(seckillOrder);
        if (result>0){
            //同步MySQL成功
            //想消息服务器返回成功通知
            try {
                /**
                 * message.getMessageProperties().getDeliveryTag()代表消息的唯一标识，第二个参数为是否开启批处理
                 */
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            //同步失败，向消息服务器返回失败通知
            try {
                /**
                 * 第一个参数为消息的唯一标识
                 * 第二个为TRUE表示所有消费者都会拒绝这个消息，FALSE表示当前的消费者会拒绝这个消息
                 * 第三个参数：TRUE当前消息会进入到死信队列（延迟消息队列），FALSE当前的消息会进入到原有队列,默认回到头部
                 */
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
