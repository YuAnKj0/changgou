package com.changgou.seach.controller;

import com.changgou.seach.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Ykj
 * @ClassName SkuController
 * @Discription
 * @date 2021/12/3 9:53
 */

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result search(){
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入数据到索引库中成功！");
    }

    @PostMapping
    public Map search(@RequestBody(required = false) Map searchMap){
        return skuService.search(searchMap);
    }
}
