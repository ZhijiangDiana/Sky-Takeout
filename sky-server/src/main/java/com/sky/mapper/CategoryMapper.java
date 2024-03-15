package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    @AutoFill(value = OperationType.INSERT)
    void insert(Category category);

    Page<Category> selectPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void delete(@Param("id") Long id);

    List<Category> selectByCondition(@Param("type") Integer type);
}
