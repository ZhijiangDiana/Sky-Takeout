package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.CategoryConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    @Override
    public PageResult pageSelect(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> res = categoryMapper.selectPage(categoryPageQueryDTO);

        return new PageResult(res.getTotal(), res.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateStatus(Long id, Integer status) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    @Override
    @Transactional()
    public void delete(Long id) {
        Category category = categoryMapper.selectById(id);
        categoryMapper.delete(id);
        // 删除一个分类时，需要同时删除其下属的所有菜品
        // 级联删除模式，可以改为限制删除
        if (CategoryConstant.DISH_TYPE.equals(category.getType()))
            dishMapper.deleteByCatId(id);
        else if (CategoryConstant.SETMEAL.equals(category.getType()))
            setmealMapper.deleteByCatId(id);

    }

    @Override
    public void insert(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public List<Category> conditionSelect(Integer type) {
        return categoryMapper.selectByCondition(type);
    }
}
