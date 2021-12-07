package com.changgou.page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Ykj
 * @ClassName PageApplication
 * @Discription
 * @date 2021/12/7 16:50
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.goods.feign"})
public class PageApplication {
    public static void main(String[] args) {
        SpringApplication.run(PageApplication.class,args);

    }
}
