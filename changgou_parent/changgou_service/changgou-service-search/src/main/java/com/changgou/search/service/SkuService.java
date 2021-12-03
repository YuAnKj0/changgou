package com.changgou.search.service;

import java.util.Map;

public interface SkuService {


    /**
     * 搜索
     * @return
     */
    Map search(Map<String, String> searchMap);
    /**
     * 导入sku数据
     */

    void importEs();
}
