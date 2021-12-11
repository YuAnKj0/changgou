package com.changgou.web.order.controller;

import com.changgou.order.feign.CartFeign;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 23:10 2021/12/11
 * @Modified By:
 */
@Controller
@RequestMapping("/wcart")
public class CartController {
    @Autowired
    private CartFeign cartFeign;


    //查询
    public String listCartList(Model model){
        Map map = cartFeign.list();
        model.addAttribute("items",map);
        return "cart";

    }


    //tianjia
    @GetMapping("/add")
    @ResponseBody
    public Result<Map> add(String skuId,Integer num){
        cartFeign.addCart(skuId, num);
        //更新购物车数据：重新查询购物车数据
        Map map = cartFeign.list();

        return new Result<>(true, StatusCode.OK,"添加购物车成功",map);

    }
}
