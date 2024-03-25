package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
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
    @ApiOperation("条件查询订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult res = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(res);
    }

    @GetMapping("/statistics")
    @ApiOperation("订单数量统计")
    public Result<OrderStatisticsVO> orderStatistics() {
        OrderStatisticsVO res = orderService.orderStatusCnt();
        return Result.success(res);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrdersPageQueryVO> getOrderById(@PathVariable Long id) {
        OrdersPageQueryVO res = orderService.getOrderById(id);
        return Result.success(res);
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String> confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirmOrder(ordersConfirmDTO.getId());
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String> rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }
}
