package com.sky.service.impl;

import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
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
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        StringBuilder dateListSbd = new StringBuilder();
        StringBuilder newUserTotalSbd = new StringBuilder();
        StringBuilder userTotalSbd = new StringBuilder();
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        // 添加日期区间之内的所有日期到dateList, 同时拼接日期字符串到dateListSbd
        while (!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            dateListSbd.append(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(",");
            currentDate = currentDate.plusDays(1);
        }
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
                .dateList(dateListSbd.substring(0, dateListSbd.length() - 1))
                .totalUserList(userTotalSbd.substring(0, userTotalSbd.length() - 1))
                .newUserList(newUserTotalSbd.substring(0, newUserTotalSbd.length() - 1))
                .build();
        return userReportVO;
    }
}
