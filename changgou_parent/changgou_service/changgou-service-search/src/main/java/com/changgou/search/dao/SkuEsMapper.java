package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Ykj
 * @ClassName SkuEsMapper
 * @Discription    该接口主要用于索引数据操作，主要使用它来实现将数据导入到ES索引库
 * @date 2021/12/3 9:39
 */
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {

}
