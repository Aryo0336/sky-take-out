package com.sky.mapper;

import com.sky.entity.Sales;
import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReportMapper {


    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    List<Long> getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计销量top10的菜品
     * @param begin
     * @param end
     */
    List<Sales> getTop10(LocalDate begin, LocalDate end);

    /**
     * 营业额统计
     * @param begin
     * @param end
     */
    Long getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计所有订单,
     * 0索引:订单总数,
     * 1索引:有效订单数
     */
    Integer[] getAllOrderStatistics();

    /**
     * 统计当天订单,
     * 0索引:订单总数,
     * 1索引:有效订单数
     * @param begin
     * @param end
     * @return
     */
    Integer[] getTodayOrderStatistics(LocalDate begin, LocalDate end);
}