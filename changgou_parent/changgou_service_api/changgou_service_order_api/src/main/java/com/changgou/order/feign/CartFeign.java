package com.changgou.order.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 23:02 2021/12/11
 * @Modified By:
 */

@FeignClient(name = "order")
public interface CartFeign {

    @GetMapping("/cart/addCart")
    public Result addCart(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num);

    @GetMapping("/cart/list")
    public Map list();
}
