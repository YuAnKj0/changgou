package com.changgou.seckill.controller;

import com.changgou.seckill.config.TokenDecode;
import com.changgou.seckill.entity.Result;
import com.changgou.seckill.entity.StatusCode;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillorder")
public class SeckillOrderController {
    @Autowired
    private TokenDecode tokenDecode;
    @Autowired
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/add")
    public Result add(@RequestParam("time") String time,@RequestParam("id") Long id){
        //1.动态的获取当前的登录人
        String username = tokenDecode.getUserInfo().get("username");
        //2。基于业务层秒杀下单
        boolean result = seckillOrderService.add(id, time, username);

        //3.放回结果
        if (result==true){
            //下单成功
            return new Result(true, StatusCode.OK,"下单成功");
        }else {
            //下单失败
            return new Result(false,StatusCode.ERROR,"下单失败");
        }

    }

}
