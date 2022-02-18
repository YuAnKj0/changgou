package com.changgou.seckill.web.controller;

import com.changgou.seckill.entity.Result;
import com.changgou.seckill.feign.SeckillGoodsFeign;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecKillGoodsController {

    @Autowired
    private SeckillGoodsFeign seckillGoodsFeign;
    @RequestMapping("/list")
    @ResponseBody
    public Result<List<SeckillGoods>> list(String time){
        return seckillGoodsFeign.list(time);

    }


    @RequestMapping(value = "/timeMenus")
    @ResponseBody
    public List<String> dataMenus(){
        List<Date> dateMenus= DateUtil.getDateMenus();
        List<String> result=new ArrayList<>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Date dateMenu : dateMenus) {
            String format = simpleDateFormat.format(dateMenu);
            result.add(format);
        }
        return result;
    }
}
