package com.changgou.user.dao;

import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

    /**
     * 增加用户积分
     * @param username
     * @param points
     * @return
     */
    @Update("update tb_user set points=points+#{points} where username=#{username}")
    int addPoints(@Param("username")String username,@Param("points") Integer points);
}
