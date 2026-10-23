package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.HistoryOrderVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    // 生成订单号: 时间戳 + 用户id + 随机数
    private String getOrdersNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        Long timeTemp = System.currentTimeMillis();
        Long userId = BaseContext.getCurrentId();
        int random = new Random().nextInt(10000, 99999);
        stringBuilder.append(timeTemp).append(userId).append(random);
        return stringBuilder.toString();
    }

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("用户id为空");
        }
        User user = userMapper.getById(userId);
        AddressBook addressBook = addressBookMapper.getById(orders.getAddressBookId());

        // 1.设置订单信息, 生成订单
        String ordersNumber = getOrdersNumber(); // 生成订单号
        LocalDateTime now = LocalDateTime.now(); // 获取下单时间
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT); // 设置订单状态为待付款
        orders.setNumber(ordersNumber); // 设置订单号
        orders.setOrderTime(now);
        orders.setPayStatus(Orders.UN_PAID); // 设置付款状态为未支付
        orders.setAddress(addressBook.getDetail()); // 详细地址
        orders.setPhone(addressBook.getPhone());
        orders.setUserName(user.getName()); // 用户名
        orders.setConsignee(addressBook.getConsignee()); // 收货人
        orderMapper.submit(orders);

        // 2.添加购物车中的商品到订单明细表中, 清空购物车
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        // 获取购物车中的商品
        List<ShoppingCart> carts = shoppingCartMapper.list(shoppingCart);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : carts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId()); // 设置订单id
            orderDetails.add(orderDetail);
        }
        orderMapper.addOrderDetail(orderDetails);
        shoppingCartMapper.clean(shoppingCart); // 清空购物车

        // 3.返回订单信息
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderTime(now);
        return orderSubmitVO;
    }

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = new PageResult();
        // 开启分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId()); // 设置当前用户id
        // 1.查询历史订单(不含订单明细)
        Page<Orders> page = orderMapper.getHistoryOrders(ordersPageQueryDTO);
        long total = page.getTotal();
        List<Orders> orders = page.getResult();
        pageResult.setTotal(total);
        // 2.查询订单明细
        List<OrderVO> orderVOS = new ArrayList<>();
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                Long orderId = order.getId();
                // 订单明细
                List<OrderDetail> orderDetailList = orderMapper.getHistoryOrderDetails(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOS.add(orderVO);
            }
        }
        pageResult.setRecords(orderVOS);
        return pageResult;
    }

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetailById(Long id) {
        // 1.获取订单信息(不含明细)
        OrderVO orderVO = orderMapper.getOrderDetailById(id);
        // 2.获取订单明细
        List<OrderDetail> orderDetail = orderMapper.getHistoryOrderDetails(id);
        orderVO.setOrderDetailList(orderDetail);
        return orderVO;
    }
}
