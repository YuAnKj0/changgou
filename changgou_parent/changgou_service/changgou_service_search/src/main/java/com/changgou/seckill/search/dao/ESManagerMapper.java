package com.changgou.seckill.search.dao;

import com.changgou.seckill.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
