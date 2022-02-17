package com.changgou.seckill;

import com.changgou.seckill.interceptor.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.changgou.seckill.feign")
public class WebSecKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSecKillApplication.class,args);
    }

    @Bean
    public FeignInterceptor feignInterceptor(){
        return new FeignInterceptor();
    }

    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        //1.创建redisTemplate模板
        RedisTemplate<Object,Object> template=new RedisTemplate<>();
        //2.关联redisConnectionFactory
        template.setConnectionFactory(redisConnectionFactory);
        //3.创建序列化类
        GenericToStringSerializer genericToStringSerializer=new GenericToStringSerializer<>(Object.class);
        //序列化类，对象映射设置
        //7.设置 value 的转化格式和 key 的转化格式
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericToStringSerializer);
        template.afterPropertiesSet();
        return template;

    }
}
