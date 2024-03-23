package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addToShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 判断商品是否已经存在
        List<ShoppingCart> res = shoppingCartMapper.select(shoppingCart);
        if (res != null && !res.isEmpty()) {
            // 存在则数量+1
            ShoppingCart sc = res.get(0);
            sc.setNumber(sc.getNumber() + 1);
            shoppingCartMapper.update(sc);
        } else {
            // 不存在则新增条目
            if (shoppingCart.getDishId() != null) {
                Dish dish = dishMapper.selectById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else if (shoppingCart.getSetmealId() != null) {
                Setmeal setmeal = setmealMapper.selectById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> listShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        return shoppingCartMapper.select(shoppingCart);
    }

    @Override
    public void clearShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        shoppingCartMapper.delete(shoppingCart);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 判断商品是否存在购物车
        ShoppingCart res = shoppingCartMapper.selectOne(shoppingCart);
        if (res == null)
            return;

        // 判断商品数量是否是1
        if ((Integer.valueOf(1).equals(res.getNumber()))) {
            // 为1直接删除
            shoppingCartMapper.delete(res);
        } else {
            // 不为1直接-1
            res.setNumber(res.getNumber() - 1);
            shoppingCartMapper.update(res);
        }
    }
}
