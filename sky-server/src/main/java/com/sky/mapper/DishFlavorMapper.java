package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insert(DishFlavor flavor);

    List<DishFlavor> selectByDishId(@Param("id") Long id);

    void deleteByDishId(@Param("id") Long id);
}
