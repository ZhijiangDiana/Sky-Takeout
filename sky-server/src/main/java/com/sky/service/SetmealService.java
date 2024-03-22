package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void insert(SetmealDTO setmealDTO);

    PageResult pageSelect(SetmealPageQueryDTO setmealPageQueryDTO);

    void batchDelete(List<Long> ids);

    SetmealVO selectById(Long id);

    void update(SetmealDTO setmealDTO);

    void updateStatus(Long id, Integer status);

    List<Setmeal> selectByCatId(Long categoryId);

    List<DishItemVO> selectDishItemsBySetmealId(Long setmealId);
}
