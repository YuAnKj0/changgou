package com.changgou.pay.feign;


import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pay")
public interface PayFeign {

    @GetMapping("/wxpay/nativaPay")
    public Result nativaPay(@RequestParam(value = "orderId") String orderId, @RequestParam(value = "money") Integer money);

    @GetMapping("/wxpay/query/{orderId}")
    public Result quertOrder(@PathVariable("orderId") String orderId);

    @PutMapping("/wxpay/close/{orderId}")
    public Result closeOrder(@PathVariable("orderId") String orderId);
}
