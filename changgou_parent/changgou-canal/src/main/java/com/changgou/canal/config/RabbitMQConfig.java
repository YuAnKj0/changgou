package com.changgou.canal.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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

    public static final String GOODS_DOWN_EXCHANGE="goods_down_exchange";

    public static final String SEARCH_ADD_QUEUE = "search_add_queue";

    public static final String SEARCH_DEL_QUEUE = "search_del_queue";

    public static final String AD_UPDATE_QUEUE = "ad_update_queue";


    @Bean   //定义队列
    public Queue queue(){
        return new Queue(AD_UPDATE_QUEUE);
    }

    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE(){
        return new Queue(SEARCH_ADD_QUEUE);
    }

    @Bean
    public Queue SEARCH_DEL_QUEUE(){
        return new Queue(SEARCH_DEL_QUEUE);
    }

    //声明交换机
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }

    @Bean(GOODS_DOWN_EXCHANGE)
    public Exchange GOODS_DOWN_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_DOWN_EXCHANGE).durable(true).build();
    }

    @Bean
    //队列与交换机的绑定
    public Binding GOODS_UP_EXCHANGE_BINDING(@Qualifier(SEARCH_ADD_QUEUE) Queue queue,@Qualifier(GOODS_UP_EXCHANGE) Exchange exchange){

        return BindingBuilder.bind(queue).to(exchange).with("").noargs();

    }
    @Bean
    public Binding GOODS_DOWN_EXCHANGE_BINDING(@Qualifier(SEARCH_DEL_QUEUE) Queue queue,@Qualifier(GOODS_DOWN_EXCHANGE)Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }



}
