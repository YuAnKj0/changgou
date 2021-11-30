package com.changgou.goods.pojo;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 13:37 2021/11/26
 * @Modified By:
 */
public class Goods implements Serializable {
    //sku
    private Spu spu;
    //SKU集合
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "spu=" + spu +
                ", skuList=" + skuList +
                '}';
    }

    public List<Sku> getSkus() {
        return skuList;
    }

    public List<Sku> setSkus(List<Sku> skus) {
        return skus;
    }
}
