package com.sky.service.impl;

import com.sky.entity.Sales;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    /**
     * 获取日期列表及其字符串,
     * 0索引:日期列表,
     * 1索引:日期字符串
     * @param begin
     * @param end
     * @return
     */
    @NonNullDecl
    private static Object[] getDataListAndTheString(LocalDate begin, LocalDate end) {
        StringBuilder dateListSbd = new StringBuilder();
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        // 添加日期区间之内的所有日期到dateList, 同时拼接日期字符串到dateListSbd
        while (!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            dateListSbd.append(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(",");
            currentDate = currentDate.plusDays(1);
        }
        // 去除字符串末尾的逗号
        String dateListStr = dateListSbd.substring(0, dateListSbd.length() - 1);
        return new Object[]{dateList, dateListStr};
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        StringBuilder newUserTotalSbd = new StringBuilder();
        StringBuilder userTotalSbd = new StringBuilder();
        Object[] dateListAndTheStr = getDataListAndTheString(begin, end);
        List<LocalDate> dateList = (List<LocalDate>) dateListAndTheStr[0];
        for (LocalDate localDate : dateList) {
            // 目标日期加一天, 查找nextDate之前的用户总数, 和目标日期当日的新用户数
            LocalDate nextDate = localDate.plusDays(1);
            List<Long> totals = reportMapper.getUserStatistics(localDate, nextDate);
            // 0索引为新用户总数,1索引为用户总数,以逗号分隔,拼接字符串返回
            newUserTotalSbd.append(totals.get(0)).append(",");
            userTotalSbd.append(totals.get(1)).append(",");
        }
        UserReportVO userReportVO = UserReportVO.builder()
                // 去除字符串末尾的逗号
                .dateList((String) dateListAndTheStr[1])
                .totalUserList(userTotalSbd.substring(0, userTotalSbd.length() - 1))
                .newUserList(newUserTotalSbd.substring(0, newUserTotalSbd.length() - 1))
                .build();
        return userReportVO;
    }

    /**
     * 统计销量top10的菜品
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        StringBuilder nameList = new StringBuilder();
        StringBuilder numberList = new StringBuilder();
        List<Sales> top10 = reportMapper.getTop10(begin, end.plusDays(1));
        if (top10 != null && !top10.isEmpty()) {
            for (Sales sales : top10) {
                nameList.append(sales.getName()).append(",");
                numberList.append(sales.getTotal()).append(",");
            }
        }
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameList.toString())
                .numberList(numberList.toString())
                .build();
        return salesTop10ReportVO;
    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        StringBuilder turnoverList = new StringBuilder();
        Object[] dataListAndTheString = getDataListAndTheString(begin, end);
        java.util.List<LocalDate> dateList = (List<LocalDate>) dataListAndTheString[0];
        // 获取每日营业额
        for (LocalDate date : dateList) {
            Long amountTotal = reportMapper.getTurnoverStatistics(date, date.plusDays(1));
            turnoverList.append(amountTotal).append(",");
        }
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList((String) dataListAndTheString[1])
                .turnoverList(turnoverList.substring(0, turnoverList.length() - 1))
                .build();
        return turnoverReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        StringBuilder orderCountList = new StringBuilder();
        StringBuilder validOrderCountList = new StringBuilder();
        Object[] dataListAndTheString = getDataListAndTheString(begin, end);
        List<LocalDate> dateList = (List<LocalDate>) dataListAndTheString[0];
        // 统计每日订单数量
        for (LocalDate date : dateList) {
            // 0索引:订单总数, 1索引:有效订单数
            Integer[] todayOrder = reportMapper.getTodayOrderStatistics(date, date.plusDays(1));
            orderCountList.append(todayOrder[0]).append(",");
            validOrderCountList.append(todayOrder[1]).append(",");
        }
        // 统计所有订单, 0索引:订单总数, 1索引:有效订单数
        Integer[] allOrderStatistics = reportMapper.getAllOrderStatistics();
        Integer orderCount = allOrderStatistics[0];
        Integer validOrderCount = allOrderStatistics[1];
        Double orderCompletionRate = validOrderCount / orderCount * 100.0;
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList((String) dataListAndTheString[1])
                .orderCountList(orderCountList.substring(0, orderCountList.length() - 1))
                .validOrderCountList(validOrderCountList.substring(0, validOrderCountList.length() - 1))
                .totalOrderCount(orderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return orderReportVO;
    }
}