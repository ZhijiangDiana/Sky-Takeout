package com.sky.controller.user;

import com.sky.annotation.FilterDisabled;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @FilterDisabled
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<Setmeal>> selectByCat(Long categoryId) {
        List<Setmeal> res = setmealService.selectByCatId(categoryId);
        return Result.success(res);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    @FilterDisabled
    public Result<List<DishItemVO>> selectSetmealDishBySetmealId(@PathVariable Long id) {
        List<DishItemVO> res = setmealService.selectDishItemsBySetmealId(id);
        return Result.success(res);
    }
}
