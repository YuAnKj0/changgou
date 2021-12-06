package com.changgou.search.controller;

import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * @author Ykj
 * @ClassName SearchController
 * @Discription
 * @date 2021/12/6 14:55
 */

@RestController

@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("")
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
