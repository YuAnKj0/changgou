package com.changgou.seckill.pay.service;

import java.util.Map;

public interface WXPayService {


    Map nativePay(String orderId,Integer money);

    //基于微信查询订单
    Map quertOrder(String orderId);

    Map closeOrder(String orderId);
}
