package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void insert(SetmealDTO setmealDTO);

    PageResult pageSelect(SetmealPageQueryDTO setmealPageQueryDTO);

    void batchDelete(List<Long> ids);

    SetmealVO selectById(Long id);

    void update(SetmealDTO setmealDTO);
}
