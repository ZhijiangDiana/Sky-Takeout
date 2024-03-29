package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.utils.DBDataUtils;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Double turnover = DBDataUtils.nullToZero(ordersMapper.selectSumByDate(begin, end));
        Integer validCnt = DBDataUtils.nullToZero(ordersMapper.selectValidCntByDate(begin, end));
        Integer allCnt = DBDataUtils.nullToZero(ordersMapper.selectCntByDate(begin, end));
        Integer newUser = DBDataUtils.nullToZero(userMapper.selectCntByDate(begin, end));

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validCnt)
                .orderCompletionRate(DBDataUtils.calculateRate(validCnt, allCnt))
                .unitPrice(DBDataUtils.calculateRate(turnover, validCnt))
                .newUsers(newUser)
                .build();
    }

    @Override
    public OrderOverViewVO getOrderOverView() {
        LocalDateTime begin = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

        return OrderOverViewVO.builder()
                .waitingOrders(DBDataUtils.nullToZero(ordersMapper.selectCntByStatus(begin, end, Orders.TO_BE_CONFIRMED)))
                .deliveredOrders(DBDataUtils.nullToZero(ordersMapper.selectCntByStatus(begin, end, Orders.CONFIRMED)))
                .completedOrders(DBDataUtils.nullToZero(ordersMapper.selectCntByStatus(begin, end, Orders.COMPLETED)))
                .cancelledOrders(DBDataUtils.nullToZero(ordersMapper.selectCntByStatus(begin, end, Orders.CANCELLED)))
                .allOrders(DBDataUtils.nullToZero(ordersMapper.selectCntByDate(begin, end)))
                .build();
    }

    @Override
    public DishOverViewVO getDishOverView() {
        return DishOverViewVO.builder()
                .sold(DBDataUtils.nullToZero(dishMapper.selectCntByStatus(StatusConstant.ENABLE)))
                .discontinued(DBDataUtils.nullToZero(dishMapper.selectCntByStatus(StatusConstant.DISABLE)))
                .build();
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {
        return SetmealOverViewVO.builder()
                .sold(DBDataUtils.nullToZero(setmealMapper.selectCntByStatus(StatusConstant.ENABLE)))
                .discontinued(DBDataUtils.nullToZero(setmealMapper.selectCntByStatus(StatusConstant.DISABLE)))
                .build();
    }
}
