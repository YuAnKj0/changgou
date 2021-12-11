package com.changgou.web.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 22:58 2021/12/11
 * @Modified By:
 */

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.order.feign" })
public class WebOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebOrderApplication.class,args);
    }
}
