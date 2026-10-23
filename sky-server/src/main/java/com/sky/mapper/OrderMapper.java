package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
