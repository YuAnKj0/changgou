package com.changgou.seckill.goods.controller;

import com.changgou.seckill.goods.pojo.Brand;
import com.changgou.seckill.goods.service.BrandService;
import com.changgou.seckill.entity.PageResult;
import com.changgou.seckill.entity.Result;
import com.changgou.seckill.entity.StatusCode;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/brand")
public class BrandController {


    @Autowired
    private BrandService brandService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        try {
            System.out.println("准备休眠，测试并发数量:"+Thread.currentThread().getId());
            Thread.sleep(10000);
            System.out.println("休眠结束:"+Thread.currentThread().getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Brand> brandList = brandService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",brandList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Brand brand = brandService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",brand);
    }


    /***
     * 新增数据
     * @param brand
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param brand
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Brand brand,@PathVariable Integer id){
        brand.setId(id);
        brandService.update(brand);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        brandService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Brand> list = brandService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Brand> pageList = brandService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    @GetMapping("/category/{categoryName}")
    public Result<List<Map>> findListByCategoryName(@PathVariable String categoryName){
        List<Map> brandList=brandService.findListByCategoryName(categoryName);
        return new Result<>(true,StatusCode.OK,"查询成功",brandList);
    }

    @GetMapping("/category/{id}")
    public Result<List<Brand>> findByCategory(@PathVariable Integer id){

        List<Brand> categoryList = brandService.findByCategory(id);
        return new Result<>(true,StatusCode.OK,"查询陈工",categoryList);
    }


}
