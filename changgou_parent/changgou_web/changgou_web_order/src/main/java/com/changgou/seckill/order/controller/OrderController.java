package com.changgou.seckill.order.controller;

import com.changgou.seckill.entity.Result;
import com.changgou.seckill.order.feign.CartFeign;
import com.changgou.seckill.order.feign.OrderFeign;
import com.changgou.seckill.order.pojo.Order;
import com.changgou.seckill.order.pojo.OrderItem;
import com.changgou.seckill.user.feign.AddressFeign;
import com.changgou.seckill.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName OrderController
 * @Discription
 * @date 2021/12/13 10:37
 */

@Controller
@RequestMapping("/worder")
public class OrderController {
    @Autowired
    private AddressFeign addressFeign;

    @Autowired
    private CartFeign cartFeign;


    @RequestMapping("/ready/order")
    public String readyOrder(Model model){
        //收件人的地址信息
        List<Address> addressList = addressFeign.list().getData();
        model.addAttribute("address",addressList);

        //购物车的信息()
        Map map = cartFeign.list();
        List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
        Integer totalMoney = (Integer) map.get("totalMoney");
        Integer totalNum = (Integer) map.get("totalNum");
        model.addAttribute("carts",orderItemList);
        model.addAttribute("totalMoney",totalMoney);
        model.addAttribute("totalNum",totalNum);

        //加载默认
        //收件人的
        for (Address address : addressList) {
            if ("1".equals(address.getIsDefault())) {
                //默认收件人
                model.addAttribute("deAddr",address);
                break;
            }
        }
        return "order";
    }
    @Autowired
    private OrderFeign orderFeign;

    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody Order order){
        Result result = orderFeign.add(order);
        return result;
    }

    @GetMapping("/toPayPage")
    public String toPayPage(String orderId,Model model){
        //获取订单的相关信息
        Order order = orderFeign.findById(orderId).getData();
        model.addAttribute("orderId",orderId);
        model.addAttribute("payMoney",order.getPayMoney());

        return "pay";
    }
}
