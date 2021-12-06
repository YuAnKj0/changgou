package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
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

@Service
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
    public void importEs() {
        //调用changgou-service-goods微服务  符合条件的sku的数据
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        List<Sku> data=skuListResult.getData(); //sku的列表
        ////将sku的列表 转换成es中的skuinfo的列表
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            //获取规格的数据  {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            //转成MAP  key: 规格的名称  value:规格的选项的值
            Map<String,Object> specMap = JSON.parseObject(skuInfo.getSpec(),Map.class);
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
        //判断是否为空 如果 为空 给一个默认 值:华为
        if (StringUtils.isEmpty(keywords)) {
            keywords="华为";
        }
        //2.创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();

        //3.设置查询条件

        //设置分组条件，商品分类
        //商品分类的列表展示: 按照商品分类的名称来分组
        //terms  指定分组的一个别名
        //field 指定要分组的字段名
        // size 指定查询结果的数量 默认是10个
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));

        //设置分组条件，商品品牌
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(50));


        //匹配查询  先分词 再查询  主条件查询
        //参数1 指定要搜索的字段
        //参数2 要搜索的值(先分词 再搜索)
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords));

        //4.构建查询对象(封装了查询的语法)
        NativeSearchQuery query=nativeSearchQueryBuilder.build();

        //5.执行查询
        AggregatedPage<SkuInfo> skuPage=elasticsearchTemplate.queryForPage(query,SkuInfo.class);

        //获取分组结果，商品品牌
        StringTerms stringTermBrand= (StringTerms) skuPage.getAggregation("skuBrandgroup");

        //获取聚合结果  获取商品分类的列表数据
        StringTerms stringTermSpec = (StringTerms) skuPage.getAggregation("skuCategorygroup");

        List<String> categoryList=getStringsCategoryList(stringTermSpec);

        List<String> brandList= getStringsBrand(stringTermBrand);
        //6.返回结果
        Map resultMap=new HashMap();
        resultMap.put("categoryList",categoryList);
        resultMap.put("brandList",brandList);
        resultMap.put("rows",skuPage.getContent());
        resultMap.put("total",skuPage.getTotalElements());
        resultMap.put("totalPages",skuPage.getTotalPages());

        return resultMap;
    }

    /**
     * 获取品牌列表
     * @param stringTerms
     * @return
     */
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

    /**
     * 获取分类列表数据
     * @param stringTermBrand
     * @return
     */
    private List<String> getStringsBrand(StringTerms stringTermBrand) {
        List<String> brandList=new ArrayList<>();
        if (stringTermBrand!=null) {
            for (StringTerms.Bucket bucket : stringTermBrand.getBuckets()) {
                brandList.add(bucket.getKeyAsString());
            }
        }
        return brandList;

    }

}
