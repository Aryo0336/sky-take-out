package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 用户提交订单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 添加订单明细
     * @param orderDetails
     */
    void addOrderDetail(List<OrderDetail> orderDetails);

    /**
     * 历史订单查询(不含订单明细)
     * @param ordersPageQueryDTO
     */
    Page<Orders> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from sky_take_out.order_detail where order_id = #{orderId}")
    List<OrderDetail> getHistoryOrderDetails(Long orderId);

    /**
     * 根据id查询订单详情
     * @param orderId
     * @return
     */
    @Select("select * from sky_take_out.orders where id = #{orderId}")
    OrderVO getById(Long orderId);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from sky_take_out.orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 获取订单数量统计
     */
    OrderStatisticsVO getStatistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Update("update sky_take_out.orders set status = #{status} where id = #{id}")
    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Update("update sky_take_out.orders set status = #{status},rejection_reason = #{dto.rejectionReason} " +
            "where id = #{dto.id}")
    void orderRejection(@Param("dto") OrdersRejectionDTO ordersRejectionDTO, int status);

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @param status
     * @param cancelTime
     */
    @Update("update sky_take_out.orders " +
            "set status = #{status}, cancel_reason = #{dto.cancelReason}," +
            "cancel_time = #{cancelTime} " +
            "where id = #{dto.id}")
    void orderCancel(@Param("dto") OrdersCancelDTO ordersCancelDTO, int status, LocalDateTime cancelTime);

    /**
     * 完成订单
     * @param id
     * @param status
     * @param completeTime
     */
    @Update("update sky_take_out.orders " +
            "set status = #{status},delivery_time = #{completeTime} " +
            "where id = #{id}")
    void orderComplete(Long id, int status, LocalDateTime completeTime);

    /**
     * 开始配送
     * @param id
     * @param status
     */
    @Update("update sky_take_out.orders " +
            "set status = #{status} " +
            "where id = #{id}")
    void delivery(Long id, int status);

    /**
     * 处理超时订单
     * @param status
     * @param timeout
     */
    @Update("update sky_take_out.orders set " +
            "status = #{status}," +
            "cancel_time = #{now}," +
            "cancel_reason = #{cancelReason} " +
            "where order_time < #{timeout} and status = #{currentStatus}")
    void processTimeoutOrder(int status, int currentStatus, LocalDateTime timeout, LocalDateTime now, String cancelReason);

    /**
     * 处理配送状态异常订单
     * @param status
     */
    @Update("update sky_take_out.orders set status = #{status} where status = #{deliveryStatus}")
    void processDeliveringOrder(int status, int deliveryStatus);
}
