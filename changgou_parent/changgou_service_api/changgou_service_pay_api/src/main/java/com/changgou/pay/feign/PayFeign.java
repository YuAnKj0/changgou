package com.changgou.pay.feign;


import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pay")
public interface PayFeign {

    @GetMapping("/wxpay/nativaPay")
    public Result nativaPay(@RequestParam(value = "orderId") String orderId, @RequestParam(value = "money") Integer money);
}
