package com.changgou.seckill;

import com.changgou.seckill.entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 16143
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.goods.dao"})
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run( GoodsApplication.class);
    }

    /***
     * IdWorker
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }


}
