package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Orders;
import com.sky.vo.OrdersPageQueryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper {
    void insert(Orders orders);

    void update(Orders orders);

    Orders selectOne(Orders ordersQuery);

    Page<OrdersPageQueryVO> selectPage(Orders orders);
}
