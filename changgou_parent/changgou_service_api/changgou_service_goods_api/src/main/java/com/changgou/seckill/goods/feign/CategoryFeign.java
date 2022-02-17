package com.changgou.seckill.goods.feign;

import com.changgou.seckill.goods.pojo.Category;
import com.changgou.seckill.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")

public interface CategoryFeign {


    @GetMapping("/category/{id}")
    public Result<Category> findById(@PathVariable Integer id);
}
