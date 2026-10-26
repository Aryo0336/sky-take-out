package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private WebSocketServer webSocketServer;

    // websocket消息类型
    private static final Integer HAS_NEW_ORDER = 1; // 来单提醒
    private static final Integer REMINDER_ORDER = 2; // 客户催单

    // 生成订单号: 时间戳 + 用户id + 随机数
    private String getOrdersNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        Long timeTemp = System.currentTimeMillis();
        Long userId = BaseContext.getCurrentId();
        int random = new Random().nextInt(10000, 99999);
        stringBuilder.append(timeTemp).append(userId).append(random);
        return stringBuilder.toString();
    }

    // 向管理端推送订单消息
    private void sendMessageToAdmin(Long orderId, String msg) {
        JSONObject msgJson = new JSONObject();
        msgJson.put("type", HAS_NEW_ORDER);
        msgJson.put("orderId", orderId);
        msgJson.put("content", msg);
        String toAdminMsg = msgJson.toString();
        webSocketServer.sendToAllClient(toAdminMsg);
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
     * 再来一单
     * @param id
     */
    @Override
    public void repetitionSubmit(Long id) {
        // 查询历史订单明细
        List<OrderDetail> orderDetail = orderMapper.getHistoryOrderDetails(id);
        // 添加菜品到购物车
        for (OrderDetail detail : orderDetail) {
            // 创建新购物车
            ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
            // 设置单个菜品信息
            shoppingCartDTO.setDishId(detail.getDishId());
            shoppingCartDTO.setSetmealId(detail.getSetmealId());
            shoppingCartDTO.setDishFlavor(detail.getDishFlavor());
            shoppingCartService.add(shoppingCartDTO);
        }
    }

    /**
     * 取消订单
     * @param orderId
     */
    @Override
    public void cancel(Long orderId, OrdersCancelDTO ordersCancelDTO) {
        // 查询订单
        Orders order = orderMapper.getById(orderId);
        order.setCancelTime(LocalDateTime.now()); // 订单取消时间
        order.setStatus(Orders.CANCELLED); // 订单状态, 已取消
        order.setCancelReason(ordersCancelDTO.getCancelReason()); // 订单取消原因
        orderMapper.update(order);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        //Long userId = BaseContext.getCurrentId();
        //User user = userMapper.getById(userId);

        // 微信支付功能暂时无法实现

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        JSONObject jsonObject = new JSONObject();
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 查询并设置订单id
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        Long orderId = ordersDB.getId();
        vo.setOrderId(orderId);
        // 无法实现微信支付功能, 直接修改订单状态
        paySuccess(ordersDB);
        // 向管理端发送来单提醒
        sendMessageToAdmin(orderId, "有新的订单");
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     * @param currentOrder
     */
    @Override
    public void paySuccess(Orders currentOrder) {

        // 根据订单号查询订单
        //Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        Orders ordersDB = currentOrder;
        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

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
        OrderVO orderVO = orderMapper.getById(id);
        // 2.获取订单明细
        List<OrderDetail> orderDetail = orderMapper.getHistoryOrderDetails(id);
        orderVO.setOrderDetailList(orderDetail);
        return orderVO;
    }

    /**
     * 管理端订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = new PageResult();
        // 开启分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(null); // 设置userId为null, 查询所有订单
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
                // 订单菜品(String)
                StringBuilder orderDishes = new StringBuilder();
                for (OrderDetail orderDetail : orderDetailList) {
                    String name = orderDetail.getName();
                    Integer number = orderDetail.getNumber();
                    orderDishes.append(name).append("x").append(number).append(";\r\n");
                }
                orderVO.setOrderDishes(orderDishes.toString());
                orderVOS.add(orderVO);
            }
        }
        pageResult.setRecords(orderVOS);
        return pageResult;
    }

    /**
     * 获取订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO getStatistics() {
        return orderMapper.getStatistics();
    }

    /**
     * 根据订单id查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getDetails(Long id) {
        return getOrderDetailById(id);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        orderMapper.confirmOrder(ordersConfirmDTO);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void orderRejection(OrdersRejectionDTO ordersRejectionDTO) {
        int status = Orders.CANCELLED;
        orderMapper.orderRejection(ordersRejectionDTO, status);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void orderCancel(OrdersCancelDTO ordersCancelDTO) {
        int status = Orders.CANCELLED;
        LocalDateTime cancelTime = LocalDateTime.now();
        orderMapper.orderCancel(ordersCancelDTO, status, cancelTime);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void orderComplete(Long id) {
        int status = Orders.COMPLETED;
        LocalDateTime completeTime = LocalDateTime.now();
        orderMapper.orderComplete(id, status, completeTime);
    }

    /**
     * 开始配送
     * @param id
     */
    @Override
    public void delivery(Long id) {
        int status = Orders.DELIVERY_IN_PROGRESS;
        orderMapper.delivery(id, status);
    }

    /**
     * 客户催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        sendMessageToAdmin(id, "客户催单");
    }
}
