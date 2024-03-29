package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(value = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("")
    @ApiOperation("新增菜品")
    public Result<String> insert(@RequestBody DishDTO dishDTO) {
        dishService.insert(dishDTO);
        cleanCache("dish_" + dishDTO.getCategoryId());

        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageSelect(DishPageQueryDTO dishPageQueryDTO) {
        PageResult res = dishService.pageSelect(dishPageQueryDTO);
        return Result.success(res);
    }

    @DeleteMapping("")
    @ApiOperation("删除菜品")
    public Result<String> delete(@RequestParam List<Long> ids) {
        dishService.delete(ids);
        cleanCache("dish_*");

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询单个菜品")
    public Result<DishVO> idSelect(@PathVariable Long id) {
        DishVO res = dishService.idSelect(id);
        return Result.success(res);
    }

    @PutMapping("")
    @ApiOperation(("修改菜品"))
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        dishService.update(dishDTO);
        cleanCache("dish_*");

        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改起售状态")
    public Result<String> updateStatus(Long id, @PathVariable Integer status) {
        dishService.updateStatus(id, status);
        cleanCache("dish_*");

        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类查询菜品")
    public Result<List<Dish>> catIdSelect(Integer categoryId) {
        List<Dish> res = dishService.catIdSelect(categoryId);
        return Result.success(res);
    }

    /**
     * 清理所有缓存
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
//        log.info("key={}缓存失效", pattern);
    }
}
