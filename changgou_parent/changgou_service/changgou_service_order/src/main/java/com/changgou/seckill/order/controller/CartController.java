package com.changgou.seckill.order.controller;

import com.changgou.seckill.order.config.TokenDecode;
import com.changgou.seckill.order.service.CartService;
import com.changgou.seckill.entity.Result;
import com.changgou.seckill.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 20:01 2021/12/11
 * @Modified By:
 */

@RestController
@RequestMapping("/cart")

public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;


    @GetMapping("/addCart")
    public Result addCart(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num){
        //动态获取登陆人的信息，暂时静态
        //String username="itcast";
        String username = tokenDecode.getUserInfo().get("username");
        cartService.addCart(skuId, num,username);
        return new Result(true, StatusCode.OK,"加入购物车成功");

    }

    @GetMapping("/list")
    public Map list(){
        //动态获取登陆人的信息，暂时静态
        //String username="itcast";
        String username = tokenDecode.getUserInfo().get("username");
        Map map = cartService.list(username);
        return map;

    }

}
