package com.itheima.canal.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.Queue;
/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 22:50 2021/12/1
 * @Modified By:
 */

@Configuration
public class RabbitMQConfig {
    //定义队列名称
    public static final String AD_UPDATE_QUEUE="ad_update_queue";


    //声明队列
    @Bean
    public Queue queue(){
        return new Queue(AD_UPDATE_QUEUE);
    }
}
