package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void insert(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        List<DishFlavor> flavors= dishDTO.getFlavors();
        for (DishFlavor flavor : flavors) {
            if (flavor.getId() == null)
                flavor.setDishId(dish.getId());
            dishFlavorMapper.insert(flavor);
        }
    }

    @Override
    public PageResult pageSelect(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> res = dishMapper.selectPage(dishPageQueryDTO);
        for (DishVO dishVO : res)
            dishVO.setFlavors(dishFlavorMapper.selectByDishId(dishVO.getId()));

        return new PageResult(res.getTotal(), res.getResult());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void delete(@RequestParam List<Long> ids) {
        // 查询是否已启用
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        // 查询是否在套餐内
        List<Long> setmealIds = setmealDishMapper.selectSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty())
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        // 删除菜品数据
        for (Long id : ids){
            // 删除菜品
            dishMapper.deleteById(id);
            // 删除口味
            dishFlavorMapper.deleteByDishId(id);
        }

    }

    @Override
    public DishVO idSelect(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorMapper.selectByDishId(id));
        return dishVO;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void update(DishDTO dishDTO) {
        // 修改dish本体数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        // 修改口味数据
        dishFlavorMapper.deleteByDishId(dish.getId());
        log.info(dishDTO.getFlavors().toString());
        for (DishFlavor dishFlavor : dishDTO.getFlavors()) {
            dishFlavor.setDishId(dish.getId());
            dishFlavorMapper.insert(dishFlavor);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }

    @Override
    public List<Dish> catIdSelect(Integer categoryId) {
        return dishMapper.selectByCatId(categoryId);
    }

    @Override
    public List<DishVO> catIdSelectWithFlavors(Long categoryId) {
        List<DishVO> res = dishMapper.selectByCatIdWithCatName(categoryId);
        for (DishVO dishVO : res)
            dishVO.setFlavors(dishFlavorMapper.selectByDishId(dishVO.getId()));
        return res;
    }
}
