package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void insert(DishDTO dishDTO);

    PageResult pageSelect(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO idSelect(Long id);

    void update(DishDTO dishDTO);

    void updateStatus(Long id, Integer status);

    List<Dish> catIdSelect(Integer categoryId);

    List<DishVO> catIdSelectWithFlavors(Long categoryId);
}
