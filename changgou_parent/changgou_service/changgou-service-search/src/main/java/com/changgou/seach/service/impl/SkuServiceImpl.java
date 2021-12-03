package com.changgou.seach.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.pojo.Sku;
import com.changgou.seach.dao.SkuEsMapper;
import com.changgou.seach.service.SkuService;
import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;

import entity.Result;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName SkuServiceImpl
 * @Discription
 * @date 2021/12/3 9:43
 */
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SkuEsMapper skuEsMapper;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 导入sku数据到ES
     */
    @Override
    public void importSku() {
        //调用changgou-service-goods微服务
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        //将数据转换成SKU
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            Map<String,Object> specMap = JSON.parseObject(skuInfo.getSpec());
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    /**
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        //1.获取关键字
        String keywords=searchMap.get("keywords");

        if (StringUtils.isEmpty(keywords)) {
            keywords="华为";
        }
        //2.创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();

        //3.设置查询条件

        //设置分组条件，商品分类
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords));

        //4.构建查询对象
        NativeSearchQuery query=nativeSearchQueryBuilder.build();

        //5.执行查询
        AggregatedPage<SkuInfo> skuPage=elasticsearchTemplate.queryForPage(query,SkuInfo.class);

        //获取分组结果
        StringTerms stringTerms=skuPage.getAggregations("skuCategorygroup");
        List<String> categoryList=getStringsCategoryList(stringTerms);

        //6.返回结果
        Map resultMap=new HashMap();
        resultMap.put("categoryList",categoryList);
        resultMap.put("rows",skuPage.getContent());
        resultMap.put("total",skuPage.getTotalElements());
        resultMap.put("totalPages",skuPage.getTotalPages());

        return resultMap;
    }

    private List<String> getStringsCategoryList(StringTerms stringTerms){

        List<String> categoryList=new ArrayList<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                categoryList.add(keyAsString);
            }
        }
        return categoryList;
    }

}
