package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "菜品分类相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PutMapping("")
    @ApiOperation("编辑分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageSelect(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult res = categoryService.pageSelect(categoryPageQueryDTO);
        return Result.success(res);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改是否启用分类")
    public Result<String> updateStatus(Long id, @PathVariable Integer status) {
        categoryService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("")
    @ApiOperation("删除分类")
    public Result<String> delete(Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @PostMapping("")
    @ApiOperation("新增分类")
    public Result<String> insert(@RequestBody Category category) {
        categoryService.insert(category);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("条件查询分类")
    public Result<List<Category>> conditionSelect(Integer type) {
        List<Category> res = categoryService.conditionSelect(type);
        return Result.success(res);
    }
}
