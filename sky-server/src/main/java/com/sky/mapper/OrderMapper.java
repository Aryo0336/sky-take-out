package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
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

    /**
     * 根据id查询订单详情
     * @param orderId
     * @return
     */
    @Select("select * from sky_take_out.orders where id = #{orderId}")
    OrderVO getOrderDetailById(Long orderId);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from sky_take_out.orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    @Select("select * from sky_take_out.orders where id = #{orderId}")
    Orders getById(Long orderId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


}
