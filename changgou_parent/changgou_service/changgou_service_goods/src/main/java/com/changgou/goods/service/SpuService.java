package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param spu
     */
    void add(Spu spu);

    /***
     * 修改
     * @param spu
     */
    void update(Spu spu);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);

    /**
     * 保存商品
     * @param goods
     */
    void saveGoods(Goods goods);

    /**
     * 根据SPU的Id查询SPU及对应的SKU集合
     * @param spuId
     * @return
     */
    Goods findGoodsById(String spuId);

    /**
     * 审核商品
     * @param spuId
     */
    void audit(Long spuId);

    /**
     * 下架商品
     * @param spuId
     */
    void pull(Long spuId);

    /**
     * 商品上架唉
     * @param ids
     * @return
     */
    int putMany(Long[] ids);

    /**
     * 批量下架
     * @param ids
     * @return
     */
    int pullMany(Long[] ids);

    /**
     * 逻辑删除
     * @param spuId
     */
    void logicDelete(Long spuId);

    /**
     * 恢复被删除的商品(逻辑删除)
     * @param id
     */
    void restore(Long id);

    /**
     * 删除商品
     * @param spuId
     */
    void delete(Long spuId);



}
