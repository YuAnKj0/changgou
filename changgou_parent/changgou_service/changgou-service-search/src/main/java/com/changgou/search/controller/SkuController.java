package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Ykj
 * @ClassName SkuController
 * @Discription 用于接收页面传递的请求 来测试 导入数据,实现搜索的功能
 *
 * @date 2021/12/3 9:53
 */

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;

    @RequestMapping("/import")
    public Result importEs(){
        skuService.importEs();
        return new Result(true, StatusCode.OK, "导入成功");
    }

    @PostMapping
    public Map search(@RequestBody(required = false) Map searchMap){
        return skuService.search(searchMap);
    }
}
