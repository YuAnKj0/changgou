package com.changgou.seckill;


import com.changgou.seckill.config.TokenDecode;
import com.changgou.seckill.entity.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling ///定时任务的注解
@MapperScan(basePackages = {"com.changgou.seckill.dao"})

public class SecKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillApplication.class,args);
    }
    /**
     * 1.查询所有符合条件的秒杀商品
     *         1) 获取时间段集合并循环遍历出每一个时间段
     *         2) 获取每一个时间段名称,用于后续redis中key的设置
     *         3) 状态必须为审核通过 status=1
     *         4) 商品库存个数>0
     *         5) 秒杀商品开始时间>=当前时间段
     *         6) 秒杀商品结束<当前时间段+2小时
     *         7) 排除之前已经加载到Redis缓存中的商品数据
     *         8) 执行查询获取对应的结果集
     * 2.将秒杀商品存入缓存
     */

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }

    /**
     * 设置RedisTemplate的序列化设置
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        //1.创建RedisTemplate模板
        RedisTemplate<Object,Object> redisTemplate=new RedisTemplate<>();
        //2.关联redisConnectionFactory
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //3.创建序列化类
        GenericToStringSerializer genericToStringSerializer=new GenericToStringSerializer<>(Object.class);
        //4.序列化类，对象映射设置
        //5.设置Value的转化格式和key的转化格式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(genericToStringSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;

    }
    @Bean
    public TokenDecode tokenDecode(){
        return new TokenDecode();
    }
}
