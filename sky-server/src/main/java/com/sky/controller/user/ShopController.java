package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private ShopService shopService;
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> selectShopStatus() {
        Integer status = shopService.selectStatus();
        return Result.success(status);
    }
}
