package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> selectSetmealIdsByDishIds(@Param("ids") List<Long> ids);
}
