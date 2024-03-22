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

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @FilterDisabled
    public Result<List<DishVO>> selectByCat(Long categoryId) {
        List<DishVO> res = dishService.catIdSelectWithFlavors(categoryId);
        return Result.success(res);
    }

}
