package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletContext;
import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end);

    UserReportVO getUserReport(LocalDate begin, LocalDate end);

    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSaleReport(LocalDate begin, LocalDate end);

    XSSFWorkbook getExcelData(ServletContext context);
}
