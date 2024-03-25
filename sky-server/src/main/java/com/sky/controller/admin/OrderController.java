package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrdersConditionQueryVO;
import com.sky.vo.OrdersPageQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrderController")
@RequestMapping("/admin/order")
@Api(value = "订单相关接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult res = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(res);
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> orderStatistics() {
        OrderStatisticsVO res = orderService.orderStatusCnt();
        return Result.success(res);
    }

    @GetMapping("/details/{id}")
    public Result<OrdersPageQueryVO> getOrderById(@PathVariable Long id) {
        OrdersPageQueryVO res = orderService.getOrderById(id);
        return Result.success(res);
    }
}
