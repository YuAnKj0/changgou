package com.changgou.goods.feign;


import com.changgou.goods.pojo.Sku;
import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "goods")
public interface SkuFeign {
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);

    @GetMapping("/sku/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    /**
     * @param id
     * @returntyurt
     */
    @GetMapping("/sku/{id}")
    public Result<Sku> findById(@PathVariable("id") String id);

    /**
     * @param username
     * @return
     */
    @PostMapping("/sku/decr/count")
    public Result decrCount(@RequestParam String username);

    @RequestMapping("/sku/resumeStockNum")
    public Result resumeStockNum(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num);
}
