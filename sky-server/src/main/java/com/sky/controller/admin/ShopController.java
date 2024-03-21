package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result<String> updateShopStatus(@PathVariable Integer status) {
        shopService.updateStatus(status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> selectShopStatus() {
        Integer status = shopService.selectStatus();
        return Result.success(status);
    }
}
