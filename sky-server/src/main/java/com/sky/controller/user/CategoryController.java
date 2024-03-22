package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.List;

@RestController("UserCategoryController")
@RequestMapping("/user/category")
@Api(tags = "商品浏览功能接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("查询所有分类")
    public Result<List<Category>> conditionSelect(@RequestParam(required = false) Integer type) {
        List<Category> res = categoryService.conditionSelect(type);
        return Result.success(res);
    }
}
