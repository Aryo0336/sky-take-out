package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索, {}", ordersPageQueryDTO);
        PageResult orders = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(orders);
    }

    /**
     * 获取各个状态订单统计数据
     * @return
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatistics() {
        log.info("获取各个状态订单统计数据");
        OrderStatisticsVO statistics = orderService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> getDetails(@PathVariable Long id) {
        log.info("查询订单详情");
        OrderVO details = orderService.getDetails(id);
        return Result.success(details);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单, orderId:{}", ordersConfirmDTO);
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    public Result orderRejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单, {}", ordersRejectionDTO);
        orderService.orderRejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单");
        orderService.orderCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 开始配送
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        log.info("开始配送, orderId:{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result orderComplete(@PathVariable Long id) {
        log.info("完成订单");
        orderService.orderComplete(id);
        return Result.success();
    }
}
