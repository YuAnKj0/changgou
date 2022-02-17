package com.changgou.seckill.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ConfirmMessageSender implements RabbitTemplate.ConfirmCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

        public static final String MESSAGE_CONFIRM_KEY="message_confirm_";

    public ConfirmMessageSender(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 接受消息服务器返回的通知，相当于一个监听方法
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        if (ack){
            //成功通知，删除redis中的消息数据，这个redis是一个存储空间，可以是nosql或者
            redisTemplate.delete(correlationData.getId());
            redisTemplate.delete(MESSAGE_CONFIRM_KEY+correlationData.getId());
        }else {
            //失败通知,将消息从redis中获取刚才的消息内容
            redisTemplate.opsForHash().entries(MESSAGE_CONFIRM_KEY+correlationData.getId());

        }

    }

    //自定义个消息的发送方法
    public void sendMessage(String exchage, String routingKey, String message){
        //设置消息的唯一标识并存图redis中
        CorrelationData correlationData=new CorrelationData(UUID.randomUUID().toString());
        redisTemplate.opsForValue().set(correlationData.getId(),message);

        //将本次发送消息的相关元数据保存在redis中
        Map<String,String> map=new HashMap<>();
        map.put("exchage",exchage);
        map.put("routingKey",routingKey);
        map.put("message",message);
        redisTemplate.opsForHash().putAll(MESSAGE_CONFIRM_KEY+correlationData.getId(),map);

        //携带本次消息的唯一标识，进行数据发送
        rabbitTemplate.convertAndSend(exchage,routingKey,message,correlationData);


    }



}
