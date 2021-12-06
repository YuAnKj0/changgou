package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapers;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName ESManagerServiceImpl
 * @Discription
 * @date 2021/12/6 10:10
 */
@Service
public class ESManagerServiceImpl implements ESManagerService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private ESManagerMapers esManagerMapers;

    //创建索引库结构
    @Override
    public void createMappingAndIndex() {
        //创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        //创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    @Override
    public void importAll() {
        ///查询sku集合
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        if (skuList==null||skuList.size()<=0) {
            throw new RuntimeException("当前没有数据被查询到，无法导入索引库");
        }

        //skuList转为json
        String jsonSkuList= JSON.toJSONString(skuList);
        //将json串转换为skuInfo
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            //将规格信息转换为map
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);

        }
        //导入索引库
        esManagerMapers.saveAll(skuInfoList);


    }

    /**
     * 根据spuid查询skuList，添加到索引库
     * @param spuId
     */
    @Override
    public void importDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList==null||skuList.size()<=0) {
            throw new RuntimeException("档期那没有数据被查询到，无法导入索引库");
        }

        String jsonSkuList = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);

        }
        esManagerMapers.saveAll(skuInfoList);

    }

    @Override
    public void delDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList==null|| skuList.size()<=0) {
            throw new RuntimeException("档期那没有数据被查询到，无法导入索引库");
        }

        for (Sku sku : skuList) {
            esManagerMapers.deleteById(Long.parseLong(sku.getId()));

        }
    }
}
