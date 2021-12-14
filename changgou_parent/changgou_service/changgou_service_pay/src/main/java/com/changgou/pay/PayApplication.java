package com.changgou.pay;

import com.github.wxpay.sdk.MyConfig;
import com.github.wxpay.sdk.WXPay;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

/**
 * @author Ykj
 * @ClassName PayApplication
 * @Discription
 * @date 2021/12/14 16:06
 */

@SpringBootApplication
@EnableEurekaClient
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }

    @Bean
    public WXPay wxPay(){
        return new WXPay(new MyConfig());
    }
}
