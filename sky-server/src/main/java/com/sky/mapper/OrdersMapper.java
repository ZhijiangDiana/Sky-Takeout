package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrdersConditionQueryVO;
import com.sky.vo.OrdersPageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface OrdersMapper {
    void insert(Orders orders);

    void update(Orders orders);

    Orders selectOne(Orders ordersQuery);

    Page<OrdersPageQueryVO> selectPage(Orders orders);

    Page<OrdersConditionQueryVO> selectCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    Integer selectCnt(Orders ordersQuery);

    Double selectSumByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Integer selectCntByDate(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    Integer selectValidCntByDate(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    Integer selectCntByStatus(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end, @Param("status") Integer status);
}
