package com.changgou.search.controller;

import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * @author Ykj
 * @ClassName SearchController
 * @Discription
 * @date 2021/12/6 14:55
 */

@Controller

@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/list")
    public String list(@RequestParam Map<String,String> searchMap, Model model){
        //特殊符号的处理
        this.handelSearhMap(searchMap);

        //获取查询结果
        Map resultMap = searchService.search(searchMap);

        model.addAttribute("result",resultMap);
        model.addAttribute("searchMap",searchMap);


        return "search";
    }


    @GetMapping
    @ResponseBody
    public Map searh(@RequestParam Map<String,String> searchMap){
        //特殊符号的处理
        this.handelSearhMap(searchMap);


        Map searchResult = searchService.search(searchMap);
        return searchResult;

    }

    private void handelSearhMap(Map<String, String> searchMap) {
        Set<Map.Entry<String, String>> entries =searchMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().startsWith("spec_")) {
                searchMap.put(entry.getKey(),entry.getValue().replace("+","%2B"));
            }
        }
    }


}
