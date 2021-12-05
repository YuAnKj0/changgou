package com.changgou.goods.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.Page;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/spu")
public class SpuController {


    @Autowired
    private SpuService spuService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Spu> spuList = spuService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",spuList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Spu spu = spuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",spu);
    }


    /***
     * 新增数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Spu spu){
        spuService.add(spu);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Spu spu,@PathVariable Long id){
        spu.setId(id);
        spuService.update(spu);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        spuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Spu> list = spuService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * 根据查询的条件 分页查询 并返回分页结果即可。
     * 分页查询 采用 pagehelper ，条件查询  通过map进行封装传递给后台即可。
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Spu> pageList = spuService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /***
     * 添加Goods
     * @param goods
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result(true,StatusCode.OK,"保存成功");
    }

    /**
     * 根据Spu的id查询good
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    public Result<Goods> findGoodsById(@PathVariable Long id){
        //根据id查询Goods(SPU+SKU)信息
        Goods goods= spuService.findGoodsById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",goods);
    }

    /**
     * 商品审核
     * @param spuId
     * @return
     */
    @PutMapping("/audit/{spuId}")
    public Result audit(@PathVariable Long spuId){
        spuService.audit(spuId);
        return new Result(true,StatusCode.OK,"商品审核成功");
    }

    /**
     * 商品下架
     * @param spuId
     * @return
     */
    @PutMapping("/pull/{spuId}")
    public Result pull(@PathVariable Long spuId){
        spuService.pull(spuId);
        return new Result(true,StatusCode.OK,"下架成功");

    }

    /**
     * 批量上架唉
     * @param ids
     * @return
     */
    @PutMapping("/put/many")
    public Result putMany(@PathVariable Long[] ids){
        int count= spuService.putMany(ids);
        return new Result(true,StatusCode.OK,"上架"+count+"个商品");
    }

    /**
     * 商品批量下架,-----------------------------------------还没测试，
     * @param ids
     * @return
     */
    @PutMapping("/pull/many")
    public Result pullMany(@PathVariable Long[] ids){
        int count = spuService.pullMany(ids);
        return new Result(true,StatusCode.OK,"成功下架"+count+"个商品");
    }

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    @DeleteMapping("/logic/delete/{id}")
    public Result logicDelete(@PathVariable Long id){
        spuService.logicDelete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 恢复数据
     * @param spuId
     * @return
     */
    @PutMapping("/restore/{spuId}")
    public Result restore(@PathVariable Long spuId){
        spuService.restore(spuId);
        return new Result(true,StatusCode.OK,"数据 恢复成功");
    }

    /**
     * 删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("/delete/{spuId}")
    public Result delete(@PathVariable Long spuId){
        spuService.delete(spuId);
        return new Result(true,StatusCode.OK,"删除成功");

    }


}
