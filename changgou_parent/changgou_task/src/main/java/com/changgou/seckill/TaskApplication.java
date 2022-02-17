package com.changgou.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  ////注解用于开启任务调度

public class TaskApplication {
    public static void main(String[] args) {
        SpringApplication.run( TaskApplication.class,args );
    }
}
