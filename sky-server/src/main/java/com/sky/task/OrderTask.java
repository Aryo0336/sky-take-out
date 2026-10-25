package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * 订单相关定时任务
 */
@Component
@Slf4j
public class OrderTask {

    String PAY_TIMEOUT = "订单支付超时";

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理支付超时订单,
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * *")
    public void processTimeoutOrder() {
        log.info("清理支付超时订单");
        // 15分钟内未支付则超时
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeout = now.plusMinutes(-15);
        // 设置订单状态为已取消
        int status = Orders.CANCELLED;
        orderMapper.processTimeoutOrder(status, timeout, now, PAY_TIMEOUT);
    }

    /**
     * 处理配送状态异常的订单,
     * 每日凌晨1点执行一次
     */
    @Scheduled(cron = "0 1 * * * *")
    public void processDeliveringOrder() {
        log.info("处理配送状态异常订单");
        int status = Orders.CANCELLED; // 设置订单状态为已取消
        int deliveryStatus = Orders.DELIVERY_IN_PROGRESS;
        orderMapper.processDeliveringOrder(status, deliveryStatus);
    }
}
