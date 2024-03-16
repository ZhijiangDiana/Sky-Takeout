package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DishMapper {
    @AutoFill(value = OperationType.UPDATE)
    void updateStatusByCatId(@Param("id") Long id, @Param("status") Integer status);

    void deleteByCatId(@Param("id") Long id);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> selectPage(DishPageQueryDTO dishPageQueryDTO);
}
