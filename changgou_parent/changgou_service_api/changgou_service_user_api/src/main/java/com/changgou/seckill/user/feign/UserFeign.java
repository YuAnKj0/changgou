package com.changgou.seckill.user.feign;


import com.changgou.seckill.entity.Result;
import com.changgou.seckill.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user")
public interface UserFeign {
    @GetMapping("/user/load/{username}")
    public User findUserInfo(@PathVariable String username);

    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(value = "points") Integer points);

}
