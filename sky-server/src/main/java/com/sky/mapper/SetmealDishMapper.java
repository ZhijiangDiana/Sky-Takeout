package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> selectSetmealIdsByDishIds(@Param("ids") List<Long> ids);

    void insert(SetmealDish setmealDish);

    List<SetmealDish> selectBySetmealId(@Param("id") Long id);

    void deleteBySetmealId(@Param("id") Long id);
}
