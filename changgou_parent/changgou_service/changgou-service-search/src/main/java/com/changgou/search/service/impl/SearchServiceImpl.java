package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ykj
 * @ClassName SearchServiceImpl
 * @Discription
 * @date 2021/12/6 13:33
 */

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search(Map<String, String> searchMap) {

        Map<String,Object> resultMap = new HashMap<>();

        //构建查询
        if (searchMap != null) {

            //构建当前的查询条件封装对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQurey = QueryBuilders.boolQuery();

            //按照关键字查询
            if (StringUtils.isNotEmpty(searchMap.get("keywords"))) {
                boolQurey.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
            }
            //按照品牌进行过滤
            if (StringUtils.isNotEmpty(searchMap.get("brand"))) {
                boolQurey.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }


            //按照品牌进行分组查询（聚合chaxun）
            String skuBrand="skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));//添加聚合条件

            //按照规格进行聚合查询
            String skuSpec="skuSPec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));


            //分页查询
            String pageNum=searchMap.get("pageNum");//当前页
            String pageSize = searchMap.get("pageSize");//每页显示多少条
            if (StringUtils.isEmpty(pageNum)) {
                pageNum="1";
            }
            if (StringUtils.isEmpty(pageSize)){
                pageSize="30";
            }
            //设置分页
            /**
             * 第一个参数：当前页。从0开始
             * 第二个参数：每页显示多少条
             */
            nativeSearchQueryBuilder.withPageable(PageRequest.
                    of(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize)));//获取pageable对象

            //按照相关字段进行排序查询
            //1.当前域，2.当前的排序操作(升序ASC，降序DESC)
            if (StringUtils.isNotEmpty(searchMap.get("sortField"))&& StringUtils.isNotEmpty(searchMap.get("sortRule"))) {
                //判断升序还是降序
                if ("ASC".equals(searchMap.get("sortField"))) {
                    //升序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort((searchMap.get("sortField"))).order(SortOrder.ASC));
                }else {
                    //降序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort((searchMap.get("sortField"))).order(SortOrder.DESC));
                }

            }

            //设置高亮域以及
            HighlightBuilder.Field field=new HighlightBuilder.Field("name")
                    //设置高亮样式
                    .preTags("<span style='color:red'>")//高亮样式前缀
                    .postTags("</span>");//后缀



            nativeSearchQueryBuilder.withHighlightFields(field);






            //按照规格进行过滤查询
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")) {
                    String value=searchMap.get(key).replace("%2B","+");

                    //设置过滤条件        spec_网络制式
                    boolQurey.filter(QueryBuilders.termQuery(("specMap."+key.substring(5)+"keyword"),value));
                }

            }
            //按照价格进行区间查询
            if (StringUtils.isNotEmpty(searchMap.get("price"))) {
                //切割price的值
                String[] prices = searchMap.get("price").split("_");
                if (prices.length==2) {
                    //0-5000de 格式
                    boolQurey.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));
                } else {
                boolQurey.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
                }
            }


            nativeSearchQueryBuilder.withQuery(boolQurey);

            //封装查询结果
            /**
             * 第一个参数：条件构建对象
             * 第二个参数：查询操作实体类
             * 第三个参数：查询结果操作对象
             */
            AggregatedPage<SkuInfo> resultInfo = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    //查询结果的相关操作
                    List<T> list=new ArrayList<>();

                    //获取查询命中结果数据
                    SearchHits hits=searchResponse.getHits();
                    if (hits!=null) {
                        for (SearchHit hit : hits) {
                            //SearchHit转换为skuinfo
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                            if (highlightFields!=null&&highlightFields.size()>0) {
                                //替换数据
                                skuInfo.setName(highlightFields.get("name").getFragments()[0].toString());
                            }
                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list,pageable,hits.getTotalHits(),searchResponse.getAggregations());
                }
            });

            //封装最终的返回结果
            //总记录数
            resultMap.put("total",resultInfo.getTotalElements());
            //总页数
            resultMap.put("totalPages",resultInfo.getTotalPages());
            //数据集合
            resultMap.put("rows",resultInfo.getContent());

            //封装品牌的分组结果
            StringTerms brandTrems = (StringTerms) resultInfo.getAggregation(skuBrand);
            //流运算
            List<String> brandList = brandTrems.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

            resultMap.put("brandList",brandList);

            //封装规格分组结果
            StringTerms specTerms = (StringTerms) resultInfo.getAggregation(skuSpec);
            List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("specList",specList);
            //当前页
            resultMap.put("pageNum",pageNum);


            return resultMap;
        }
        return null;
    }
}
