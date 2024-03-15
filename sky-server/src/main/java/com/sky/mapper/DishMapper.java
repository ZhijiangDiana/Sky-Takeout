package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DishMapper {

    void updateStatusByCatId(@Param("id") Long id, @Param("status") Integer status);

    void deleteByCatId(@Param("id") Long id);
}
