package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersPageQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("UserOrderController")
@RequestMapping("/user/order")
@Api(value = "用户端订单相关接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单接口")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO res = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(res);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
//        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult res = orderService.historyOrdersPageQuery(ordersPageQueryDTO);
        return Result.success(res);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation(("查看订单详情"))
    public Result<OrdersPageQueryVO> getOrderDetails(@PathVariable Long id) {
        OrdersPageQueryVO res = orderService.getOrderDetails(id);
//        log.info(res.toString());
        return Result.success(res);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<String> getAnotherOrder(@PathVariable Long id) {
        orderService.getAnotherOrder(id);
        return Result.success();
    }

}
