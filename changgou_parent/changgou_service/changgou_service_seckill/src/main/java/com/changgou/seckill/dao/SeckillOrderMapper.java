package com.changgou.seckill.dao;

import com.changgou.seckill.pojo.SeckillOrder;
import feign.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;


public interface SeckillOrderMapper extends Mapper<SeckillOrder> {

    /**
     *查询秒杀订单信息
     * @param username
     * @param id
     * @return
     */
    @Select("select * from tb_seckill_order where user_id=#{username} and seckill_id=#{id}")
    SeckillOrder getOrderInfoByUserNameAndGoodsId(@Param("username") String username, @Param("id")Long id);

  
}
