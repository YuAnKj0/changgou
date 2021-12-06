package com.changgou.search.controller;

import com.changgou.search.service.ESManagerService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ykj
 * @ClassName ESManagerController
 * @Discription
 * @date 2021/12/6 10:34
 */

@RestController
@RequestMapping("/manager")
public class ESManagerController {

    @Autowired
    private ESManagerService esManagerService;


    @GetMapping("/create")
    public Result create() {
        esManagerService.createMappingAndIndex();
        return new Result(true, StatusCode.OK, "导入索引库结构成功");
    }

    @GetMapping("/importAll")
    public Result importAll(){
        esManagerService.importAll();
        return new Result(true,StatusCode.OK,"导入全部数据成功");
    }


}
