package com.changgou.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Ykj
 * @ClassName ContentApplication
 * @Discription
 * @date 2021/12/2 10:03
 */

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages={"com.changgou.content.dao"})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class,args);
    }
}
