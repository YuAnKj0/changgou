package com.changgou.content.controller;

import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Ykj
 * @ClassName ContentController
 * @Discription
 * @date 2021/12/2 13:12
 */

@Controller
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;


    /**
     * 根据id查询广告集合
     * @param id
     * @return
     */
    @GetMapping("/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable Long id){
        List<Content> contents=contentService.findByCategory(id);
        return new Result<>(true, StatusCode.OK,"查询成功！",contents);
    }
}
