package com.changgou.seckill.service.impl;

import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceimpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;
    private static final String SECKILL_GOODS_KEY="seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY="seckill_goods_stock_count_";


    @Override
    public List<SeckillGoods> list(String time) {
        List<SeckillGoods> list = redisTemplate.boundHashOps(SECKILL_GOODS_KEY + time).values();
        //更新 库存数据的来源
        for (SeckillGoods seckillGoods : list) {
            String value = (String) redisTemplate.opsForValue().get(SECKILL_GOODS_STOCK_COUNT_KEY + seckillGoods.getId());
            seckillGoods.setStockCount(Integer.valueOf(value));

        }
        return list;
    }
}
