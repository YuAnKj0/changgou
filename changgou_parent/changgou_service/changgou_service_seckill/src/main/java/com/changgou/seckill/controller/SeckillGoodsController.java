package com.changgou.seckill.controller;

import com.changgou.seckill.entity.Result;
import com.changgou.seckill.entity.StatusCode;

import com.changgou.seckill.feign.SeckillGoodsFeign;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillgoods")
public class SeckillGoodsController {


    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @RequestMapping("/list")
    public Result<List<SeckillGoods>> list(@RequestParam("time") String time){
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.list(time);
        return new Result<>(true, StatusCode.OK,"查询成功",seckillGoodsList);
    }


}
