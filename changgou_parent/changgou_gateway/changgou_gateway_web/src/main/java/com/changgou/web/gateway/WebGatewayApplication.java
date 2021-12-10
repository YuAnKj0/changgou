package com.changgou.web.gateway;

import org.bouncycastle.asn1.ASN1Primitive;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Ykj
 * @ClassName WebGatewayApplication
 * @Discription
 * @date 2021/12/10 16:33
 */
@SpringBootApplication
@EnableEurekaClient
public class WebGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebGatewayApplication.class,args);

    }
}
