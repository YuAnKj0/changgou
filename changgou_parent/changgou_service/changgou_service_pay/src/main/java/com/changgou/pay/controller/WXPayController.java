package com.changgou.pay.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.service.WXPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName WXPayController
 * @Discription
 * @date 2021/12/14 17:02
 */
@RestController
@RequestMapping("wxpay")
public class WXPayController {

    @Autowired
    private WXPayService wxPayService;

    @GetMapping("/nativaPay")
    public Result nativaPay(@RequestParam(value = "orderId") String orderId,@RequestParam(value = "money") Integer money){
        Map resultMap = wxPayService.nativePay(orderId, money);
        return new Result(true, StatusCode.OK,"下单成功",resultMap);

    }

}
