package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {
    void insert(DishDTO dishDTO);

    PageResult pageSelect(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);
}
