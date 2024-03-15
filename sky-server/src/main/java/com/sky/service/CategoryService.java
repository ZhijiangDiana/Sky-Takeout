package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void update(CategoryDTO categoryDTO);

    PageResult pageSelect(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateStatus(Long id, Integer status);

    void delete(Long id);

    void insert(Category category);

    List<Category> conditionSelect(Integer type);
}
