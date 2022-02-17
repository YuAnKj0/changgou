package com.changgou.seckill.user.feign;


import com.changgou.seckill.entity.Result;
import com.changgou.seckill.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user")
public interface AddressFeign {

    @GetMapping("/address/list")
    public Result<List<Address>> list();
}
