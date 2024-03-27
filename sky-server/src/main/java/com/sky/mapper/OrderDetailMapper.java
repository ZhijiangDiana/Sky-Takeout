package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void insert(OrderDetail orderDetail);

    List<OrderDetail> select(OrderDetail orderDetailQuery);

    List<OrderDetail> selectTop10ByDate(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
