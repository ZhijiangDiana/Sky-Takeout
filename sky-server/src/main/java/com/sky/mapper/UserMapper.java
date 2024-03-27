package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    User selectOne(User user);

    User selectByOpenid(@Param("openid") String openid);

    void insert(User user);

    Integer selectCntByDate(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
