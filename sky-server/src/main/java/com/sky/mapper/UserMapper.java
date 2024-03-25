package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectOne(User user);

    User selectByOpenid(@Param("openid") String openid);

    void insert(User user);
}
