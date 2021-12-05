package com.changgou.canal.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.BindingBuilder;


/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 19:28 2021/12/5
 * @Modified By:
 */

@Configuration //声明是个配置了
public class RabbitMQConfig {


    //定义交换机名称
    public static final String GOODS_UP_EXCHANGE="goods_up_exchange";

        public static final String SEARCH_ADD_QUEUE = "search_add_queue";

    public static final String AD_UPDATE_QUEUE = "ad_update_queue";


    @Bean   //定义队列
    public Queue queue(){
        return new Queue(AD_UPDATE_QUEUE);
    }

    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE(){
        return new Queue(SEARCH_ADD_QUEUE);
    }

    //声明交换机
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }

    //队列与交换机的绑定
    public Binding GOODS_UP_EXCHANGE_BINDING(@Qualifier(SEARCH_ADD_QUEUE) Queue queue,@Qualifier(GOODS_UP_EXCHANGE) Exchange exchange){

        return BindingBuilder.bind(queue).to(exchange).with("").noargs();

    };

}
