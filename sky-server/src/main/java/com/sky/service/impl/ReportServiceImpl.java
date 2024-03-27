package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.utils.DBDataUtils;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> amountList = new ArrayList<>();

        dateList.forEach(d -> amountList.add(DBDataUtils.nullToZero(ordersMapper.selectSumByDate(d.atStartOfDay(), d.atTime(LocalTime.MAX)))));

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(amountList, ","))
                .build();
    }

    private static List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        return dateList;
    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> newUserList = new ArrayList<>();

        dateList.forEach(d -> newUserList.add(DBDataUtils.nullToZero(userMapper.selectCntByDate(d.atStartOfDay(), d.atTime(LocalTime.MAX)))));

        List<Integer> totalUserList = new ArrayList<>();
        final Integer[] previous = {0};
        newUserList.forEach(d -> {
            Integer thisDay = previous[0] + d;
            totalUserList.add(thisDay);
            previous[0] = thisDay;
        });

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        dateList.forEach(d -> orderCountList.add(DBDataUtils.nullToZero(ordersMapper.selectCntByDate(d.atStartOfDay(), d.atTime(LocalTime.MAX)))));
        Integer orderCntSum = orderCountList.stream().reduce(0, Integer::sum);

        dateList.forEach(d -> validOrderCountList.add(DBDataUtils.nullToZero(ordersMapper.selectValidCntByDate(d.atStartOfDay(), d.atTime(LocalTime.MAX)))));
        Integer validOrderCnt = validOrderCountList.stream().reduce(0, Integer::sum);

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(orderCntSum)
                .validOrderCount(validOrderCnt)
                .orderCompletionRate(DBDataUtils.calculateRate(validOrderCnt, orderCntSum))
                .build();
    }

    @Override
    public SalesTop10ReportVO getSaleReport(LocalDate begin, LocalDate end) {
        List<OrderDetail> res = orderDetailMapper.selectTop10ByDate(begin.atStartOfDay(), end.atTime(LocalTime.MAX));
//        .stream()
//                .sorted(Comparator.comparingInt(OrderDetail::getNumber).reversed())
//                .toList()

//        log.info(res.toString());

        List<String> nameList = res.stream().map(OrderDetail::getName).toList();
        List<Integer> cntList = res.stream().map(OrderDetail::getNumber).toList();

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(cntList, ","))
                .build();
    }
}
