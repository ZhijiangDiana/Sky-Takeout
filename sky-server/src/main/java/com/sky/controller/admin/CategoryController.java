package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
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
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageSelect(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult res = categoryService.pageSelect(categoryPageQueryDTO);
        return Result.success(res);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(Long id, @PathVariable Integer status) {
        categoryService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("")
    public Result<String> delete(Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @PostMapping("")
    public Result<String> insert(@RequestBody Category category) {
        categoryService.insert(category);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Category>> conditionSelect(Integer type) {
        List<Category> res = categoryService.conditionSelect(type);
        return Result.success(res);
    }
}
