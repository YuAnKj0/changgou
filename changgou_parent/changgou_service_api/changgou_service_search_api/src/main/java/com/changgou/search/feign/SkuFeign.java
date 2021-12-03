package com.changgou.search.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

/**
 * @author Ykj
 * @ClassName SkuFeign
 * @Discription
 * @date 2021/12/3 9:34
 */

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /**
     * 根据审核状态查询
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable String status);

}
