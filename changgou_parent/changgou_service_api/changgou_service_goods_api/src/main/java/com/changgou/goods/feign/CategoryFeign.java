package com.changgou.goods.feign;

import entity.Result;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")

public interface CategoryFeign {


    @GetMapping("/category/{id}")
    public Result findById(@PathVariable Integer id);
}
