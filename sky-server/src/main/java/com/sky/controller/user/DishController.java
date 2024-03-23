package com.sky.controller.user;

import com.sky.annotation.FilterDisabled;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
@Api("菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @FilterDisabled
    public Result<List<DishVO>> selectByCat(Long categoryId) {
        String key = "dish_" + categoryId;
        // 查询redis中是否存在菜品数据
        List<DishVO> res = (List<DishVO>) redisTemplate.opsForValue().get(key);
        // 如果存在，直接返回
        if (res != null && !res.isEmpty()) {
            log.info("缓存命中");
            return Result.success(res);
        }

        // 如果不存在，再查询数据库
        res = dishService.catIdSelectWithFlavors(categoryId);
        // 将结果存入redis
        redisTemplate.opsForValue().set(key, res);
        log.info("缓存未命中");

        return Result.success(res);
    }

}
