package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(value = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping("")
    @ApiOperation("新增菜品")
    public Result<String> insert(@RequestBody DishDTO dishDTO) {
        dishService.insert(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageSelect(DishPageQueryDTO dishPageQueryDTO) {
        PageResult res = dishService.pageSelect(dishPageQueryDTO);
        return Result.success(res);
    }
}