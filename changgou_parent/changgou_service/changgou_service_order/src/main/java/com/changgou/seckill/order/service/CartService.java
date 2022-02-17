package com.changgou.seckill.order.service;

import java.util.Map;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 19:36 2021/12/11
 * @Modified By:
 */
public interface CartService {


    //添加购物车
    void addCart(String skuId,Integer num,String username);

    //查询购物车数据
    Map list(String username);
}
