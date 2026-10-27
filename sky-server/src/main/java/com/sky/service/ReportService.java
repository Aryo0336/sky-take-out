package com.sky.service;

import com.sky.vo.*;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计销量top10
     * @param begin
     * @param end
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);

    /**
     * 营业额统计
     * @param begin
     * @param end
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
}
