package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper {
    void deleteByCatId(@Param("id") Long id);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> selectPage(DishPageQueryDTO dishPageQueryDTO);

    Dish selectById(@Param("id") Long id);

    void deleteById(@Param("id") Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> selectByCatId(@Param("categoryId") Integer categoryId);

    List<DishVO> selectByCatIdWithCatName(@Param("categoryId") Long categoryId);
}
