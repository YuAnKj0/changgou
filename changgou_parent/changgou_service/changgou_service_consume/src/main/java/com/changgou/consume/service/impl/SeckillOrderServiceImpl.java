package com.changgou.consume.service.impl;

import com.changgou.consume.dao.SeckillGoodsMapper;
import com.changgou.consume.dao.SeckillOrderMapper;
import com.changgou.consume.service.SeckillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;

public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    @Transactional
    public int createOrder(SeckillOrder seckillOrder) {
        //同步MySQL中的数据
        /**
         * 1.扣减秒杀商品的库存
         * 2.新增秒杀订单
         */
        int result = seckillGoodsMapper.updateStockCount(seckillOrder.getSeckillId());
        if (result<=0){
            return 0;
        }

        //新增秒杀订单
        result=seckillOrderMapper.insertSelective(seckillOrder);
        if (result<=0){
            return 0;
        }

        return 1;
    }
}
