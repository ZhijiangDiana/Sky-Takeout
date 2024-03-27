package com.sky.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.extra.OrderTleMessage;
import com.sky.mapper.OrdersMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class tleListener {

    @Autowired
    private OrdersMapper ordersMapper;

    @RabbitListener(queues = "orderTleCancel.out.queue")
    public void orderTleListener(String message) {
//        log.info(message);
        OrderTleMessage msg = JSON.parseObject(message, OrderTleMessage.class);
//        log.info(msg.toString());

        Orders orderQuery = Orders.builder().id(msg.getOrderId()).build();
        Orders orders = ordersMapper.selectOne(orderQuery);
        if (OrderTleMessage.PAY_ORDER_TLE.equals(msg.getTleType())) {
            if (orders == null || !Orders.PENDING_PAYMENT.equals(orders.getStatus()))
                return;
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelReason("订单超时未支付");
            orders.setCancelTime(LocalDateTime.now());
            ordersMapper.update(orders);
//            log.info("{}订单超时未支付", msg.getOrderId());
        } else if (OrderTleMessage.FINISH_ORDER_TLE.equals(msg.getTleType())) {
            if (orders == null || !Orders.DELIVERY_IN_PROGRESS.equals(orders.getStatus()))
                return;
            orders.setStatus(Orders.COMPLETED);
            ordersMapper.update(orders);
            log.info("{}订单自动完成", msg.getOrderId());
        }
    }
}
