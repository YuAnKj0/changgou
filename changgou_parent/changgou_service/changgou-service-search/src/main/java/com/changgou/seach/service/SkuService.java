package com.changgou.seach.service;

import java.util.Map;

public interface SkuService {
    /**
     * 导入sku数据
     */
    void importSku();

    /**
     * 搜索
     * @return
     */
    Map search(Map<String, String> searchMap);
}
