package com.changgou.seckill.pay.feign;


import com.changgou.seckill.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pay")
public interface PayFeign {

    @GetMapping("/wxpay/nativaPay")
    public Result nativaPay(@RequestParam(value = "orderId") String orderId, @RequestParam(value = "money") Integer money);

    @GetMapping("wxpay/query/{orderId}")
    public Result queryOeder(@PathVariable("orderId") String orderId);

    @GetMapping ("wxpay/close/{orderId}")
    public Result closeOrder(@PathVariable String orderId);
}
