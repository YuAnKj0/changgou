package com.changgou.seckill.pay.controller;

import com.changgou.seckill.entity.Result;
import com.changgou.seckill.entity.StatusCode;
import com.changgou.seckill.pay.config.RabbitMQConfig;
import com.changgou.seckill.pay.service.WXPayService;
import com.changgou.seckill.utils.ConvertUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public Result nativaPay(@RequestParam(value = "orderId") String orderId, @RequestParam(value = "money") Integer money){
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
                    rabbitTemplate.convertAndSend("", RabbitMQConfig.ORDER_PAY);



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
         //////////////////////////////////////////////////////////////////////////////

    //基于微信查询订单
    @GetMapping("/query/{orderId}")
    public Result quertOrder(@PathVariable("orderId") String orderId){
        Map map=wxPayService.quertOrder(orderId);
        return new Result(true,StatusCode.OK,"查询订单成功",map);
    }

    //基于微信关闭订单
    @PutMapping("/close/{orderId}")
    public Result closeOrder(@PathVariable("orderId") String orderId){
        Map map=wxPayService.closeOrder(orderId);
        return new Result(true,StatusCode.OK,"关闭订单成功",map);

    }
}

