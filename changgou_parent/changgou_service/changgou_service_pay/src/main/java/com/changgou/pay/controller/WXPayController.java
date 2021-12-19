package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.config.RabbitMQConfig;
import com.changgou.pay.service.WXPayService;
import com.changgou.utils.ConvertUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.HashMap;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/nativaPay")
    public Result nativaPay(@RequestParam(value = "orderId") String orderId,@RequestParam(value = "money") Integer money){
        Map resultMap = wxPayService.nativePay(orderId, money);
        return new Result(true, StatusCode.OK,"下单成功",resultMap);

    }

    @RequestMapping("/notify")
    public void notifyLogic(HttpServletRequest request, HttpServletResponse response){

        System.out.println("支付回调成功");
        try{
            //输入流转换为字符串
            String xml = ConvertUtils.convertToString(request.getInputStream());
            System.out.println(xml);

            //基于微信发送的通知内容，完成后续的业务逻辑处理

            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            if ("SUCESS".equals(map.get("result_code"))) {

                //查询订单
                Map result = wxPayService.quertOrder(map.get("out_trade_no"));
                System.out.println("查询订单的结果"+result);
                if ("SUCCESS".equals(map.get("result_code"))) {
                    //将订单消息发送到mq
                    Map message=new HashMap();
                    message.put("orderId",result.get("out_trade_no"));
                    message.put("transaction_id",result.get("transaction_id"));

                    //消息的发送
                    rabbitTemplate.convertAndSend("", RabbitMQConfig.ORDER_PAY, JSON.toJSONString(message));



                }


            }else {
                //输出错误原因
                System.out.println(map.get("err_code_des"));
            }



            //商家处理返回通知
            response.setContentType("text/html");
            String data="<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            response.getWriter().write(data);


        }catch (Exception e){
            e.printStackTrace();
        }






    }
}
