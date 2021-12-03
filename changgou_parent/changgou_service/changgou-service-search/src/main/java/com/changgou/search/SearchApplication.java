package com.changgou.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author Ykj
 * @ClassName SearchApplication
 * @Discription 需要开启Feign客户端，并且需要添加ES包扫描
 * @date 2021/12/3 8:52
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.changgou.search.feign")
@EnableElasticsearchRepositories(basePackages = "com.changgou.search.dao")
public class SearchApplication {
    public static void main(String[] args) {

        /**
         * Springboot整合Elasticsearch 在项目启动前设置一下的属性，防止报错
         * 解决netty冲突后初始化client时还会抛出异常
         * availableProcessors is already set to [12], rejecting [12]
         */
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(SearchApplication.class,args);
    }
}
