package com.changgou.pay.service.impl;

import com.changgou.pay.service.WXPayService;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName WXPayServieceImpl
 * @Discription
 * @date 2021/12/14 16:16
 */
@Service
public class WXPayServieceImpl implements WXPayService {

    @Autowired
    private WXPay wxPay;

    @Value("${wxpay.notify_url}")
    private String notify_url;

    /**
     * 统一下单的接口调用
     * @param orderId
     * @param money
     * @return
     */
    @Override
    public Map nativePay(String orderId, Integer money) {
        try{
            //1.封装请求参数
            Map<String, String> map=new HashMap<>();
            map.put("body","畅购商城");
            map.put("out_trade_no",orderId);
            BigDecimal payMoney = new BigDecimal("0.01");
            BigDecimal fen=payMoney.multiply(new BigDecimal("100")); //1.00
            fen.setScale(0,BigDecimal.ROUND_UP); //1

            map.put("total_fee",String.valueOf(fen));

            map.put("spbill_create_ip","127.0.0.1");
            map.put("notify_url",notify_url);
            map.put("trade_type","NATIVE");

            //2.基于wxpay完成统一接口的调用,并获取返回结果
            Map<String, String> mapResult = wxPay.unifiedOrder(map);
            return mapResult;

        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map quertOrder(String orderId) {
        try{
            Map<String,String> map=new HashMap();
            map.put("out_trado_no",orderId);
            Map<String, String> resultMap = wxPay.orderQuery(map);
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closeOrder(String orderId) {
        try{
            Map<String,String> map=new HashMap();
            map.put("out_trade_no",orderId);


            Map<String, String> resultMap = wxPay.closeOrder(map);
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
