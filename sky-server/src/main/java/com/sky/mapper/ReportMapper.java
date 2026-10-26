package com.sky.mapper;

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
}
