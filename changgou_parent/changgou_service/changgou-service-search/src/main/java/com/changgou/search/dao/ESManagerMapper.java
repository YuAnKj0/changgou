package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;



/**
 * @author 16143
 */
public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
