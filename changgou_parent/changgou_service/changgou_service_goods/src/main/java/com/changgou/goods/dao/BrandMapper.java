package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {

    /**
     *
     * @param categoryName
     * @return
     */
    @Select("SELECT name,image FROM tb_brand WHERE id IN( SELECT brand_id FROM tb_category_brand where category_id in(SELECT id FROM tb_category where name=#{categoryName}))")
    public List<Map> findListByCategoryName(@Param("categoryName") String categoryName);

    @Select("select * from tb_category_brand tcb,tb_brand tb  where tcb.category_id=#{categoryid} and tb.id=tcb.brand_id")
     List<Brand> findByCategory(@Param("categoryid") Integer categoryid);


}
