package com.changgou.seckill.goods.dao;

import com.changgou.seckill.goods.pojo.Spec;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {


    @Select("select name,options from tb_spec WHERE template_id in(SELECT template_id FROM tb_category WHERE name=#{categoryName})")
    public List<Map> findSpecListByCategoryName(@Param("categoryName") String categoryName);

}
