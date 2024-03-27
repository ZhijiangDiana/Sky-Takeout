package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.extra.OrderTleMessage;
import com.sky.mapper.*;
import com.sky.properties.WeChatProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.ws.OrderNotifyWs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderNotifyWs orderNotifyWs;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 处理业务异常
        // 地址为空
        AddressBook addQuery = AddressBook.builder().id(ordersSubmitDTO.getAddressBookId()).build();
        AddressBook addressBook = addressBookMapper.selectOne(addQuery);
        if (addressBook == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        // 购物车为空
        Long userId = BaseContext.getCurrentId();
        ShoppingCart scQuery = ShoppingCart.builder().id(userId).build();
        List<ShoppingCart> userCart = shoppingCartMapper.select(scQuery);
        if (userCart == null || userCart.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 向订单表插入1条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());
        orders.setUserId(userId);
        ordersMapper.insert(orders);

        // 向订单明细表插入n条数据
        for (ShoppingCart shoppingCart : userCart) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailMapper.insert(orderDetail);
        }

        // 清空购物车
        ShoppingCart scClearQuery = ShoppingCart.builder().userId(userId).build();
        shoppingCartMapper.delete(scClearQuery);

//        log.info("{}订单等待支付", orders.getId());
        // 将延时任务加入消息队列
        OrderTleMessage orderTleMessage = OrderTleMessage.builder()
                .tleType(OrderTleMessage.PAY_ORDER_TLE)
                .orderId(orders.getId())
                .build();
        Message msg = MessageBuilder
                .withBody(JSON.toJSONString(orderTleMessage).getBytes(StandardCharsets.UTF_8))
                .setExpiration(OrderTleMessage.PAY_TIME_MS)
                .build();
        rabbitTemplate.convertAndSend("orderTleCancel.in.exchange", "114514", msg);

        // 封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        User userQuery = User.builder().id(BaseContext.getCurrentId()).build();
        User user = userMapper.selectOne(userQuery);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = new JSONObject();
        if (weChatProperties.getEnableWeChatPay()) {
            // 正常调用微信支付接口
            jsonObject = weChatPayUtil.pay(
                    ordersPaymentDTO.getOrderNumber(), //商户订单号
                    new BigDecimal(0.01), //支付金额，单位 元
                    "苍穹外卖订单", //商品描述
                    user.getOpenid() //微信用户的openid
            );

            if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID"))
                throw new OrderBusinessException("该订单已支付");
        } else {
            // 不调用微信支付接口，5s后模拟支付成功
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                paySuccess(ordersPaymentDTO.getOrderNumber());
            }).start();
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        Orders ordersQuery = Orders.builder().number(outTradeNo).build();

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.selectOne(ordersQuery);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);

        // 向在线的管理端推送订单提醒
        JSONObject newOrderNotification = new JSONObject();
        newOrderNotification.put("type", Orders.NEW_ORDER);
        newOrderNotification.put("orderId", ordersDB.getId());
        newOrderNotification.put("content", "新订单：" + ordersDB.getNumber());
        orderNotifyWs.sendToAllClient(newOrderNotification.toJSONString());
    }

    @Override
    public PageResult historyOrdersPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersPageQueryDTO, orders);
        orders.setUserId(BaseContext.getCurrentId());
        Page<OrdersPageQueryVO> res = ordersMapper.selectPage(orders);

        List<OrdersPageQueryVO> resList = res.getResult();
        for (OrdersPageQueryVO order : resList) {
            OrderDetail orderDetailQuery = OrderDetail.builder().orderId(order.getId()).build();
            order.setOrderDetailList(orderDetailMapper.select(orderDetailQuery));
        }

        return new PageResult(res.getTotal(), resList);
    }

    @Override
    public OrdersPageQueryVO getOrderDetails(Long id) {
        Orders orderQuery = Orders.builder().id(id).userId(BaseContext.getCurrentId()).build();
        Orders orders = ordersMapper.selectOne(orderQuery);
        OrdersPageQueryVO res = new OrdersPageQueryVO();
        BeanUtils.copyProperties(orders, res);

        OrderDetail orderDetailQuery = OrderDetail.builder().orderId(res.getId()).build();
        res.setOrderDetailList(orderDetailMapper.select(orderDetailQuery));
        return res;
    }

    @Override
    public void userCancelOrder(Long id) {
        Orders orderQuery = Orders.builder().id(id).build();
        Orders order = ordersMapper.selectOne(orderQuery);

        if (order == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (order.getStatus() >= Orders.CONFIRMED || Orders.REFUND.equals(order.getPayStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        if (Orders.PAID.equals(order.getPayStatus())) {
            // TODO 退款的请求可以在此处补上
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        ordersMapper.update(order);
    }

    @Override
    public void getAnotherOrder(Long id) {
        OrderDetail orderDetailQuery = OrderDetail.builder().orderId(id).build();
        List<OrderDetail> details = orderDetailMapper.select(orderDetailQuery);
        for (OrderDetail orderDetail : details) {
            ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
            BeanUtils.copyProperties(orderDetail, shoppingCartDTO);
            for (int i = 0; i < orderDetail.getNumber(); i++)
                shoppingCartService.addToShoppingCart(shoppingCartDTO);
        }
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrdersConditionQueryVO> res = ordersMapper.selectCondition(ordersPageQueryDTO);
        List<OrdersConditionQueryVO> resList = res.getResult();
        for (OrdersConditionQueryVO o : resList) {
            OrderDetail orderDetailQuery = OrderDetail.builder().orderId(o.getId()).build();
            List<OrderDetail> details = orderDetailMapper.select(orderDetailQuery);

            StringBuilder orderedDishes = new StringBuilder();
            details.forEach(d -> orderedDishes.append(d.getName()).append(" "));
            o.setOrderDetailList(orderedDishes.toString());
        }
        return new PageResult(res.getTotal(), resList);
    }

    @Override
    public OrderStatisticsVO orderStatusCnt() {
        Orders ordersQuery = new Orders();
        ordersQuery.setStatus(Orders.TO_BE_CONFIRMED);
        Integer toBeConfirmedCnt = ordersMapper.selectCnt(ordersQuery);
        ordersQuery.setStatus(Orders.CONFIRMED);
        Integer confirmedCnt = ordersMapper.selectCnt(ordersQuery);
        ordersQuery.setStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer delivering = ordersMapper.selectCnt(ordersQuery);

        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmedCnt)
                .confirmed(confirmedCnt)
                .deliveryInProgress(delivering)
                .build();
    }

    @Override
    public OrdersPageQueryVO getOrderById(Long id) {
        Orders ordersQuery = Orders.builder().id(id).build();
        Orders orders = ordersMapper.selectOne(ordersQuery);
        OrdersPageQueryVO res = new OrdersPageQueryVO();
        BeanUtils.copyProperties(orders, res);

        OrderDetail orderDetailQuery = OrderDetail.builder().orderId(id).build();
        List<OrderDetail> details = orderDetailMapper.select(orderDetailQuery);
        res.setOrderDetailList(details);
        return res;
    }

    @Override
    public void confirmOrder(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();
        String rejectReason = ordersRejectionDTO.getRejectionReason();

        Orders ordersQuery = Orders.builder().id(id).build();
        Orders orderDB = ordersMapper.selectOne(ordersQuery);

        if (orderDB == null || !Orders.TO_BE_CONFIRMED.equals(orderDB.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .rejectionReason(rejectReason)
                .cancelTime(LocalDateTime.now())
                .build();
        if (Orders.PAID.equals(orderDB.getPayStatus())) {
            // TODO 调用微信支付退款
            orders.setPayStatus(Orders.REFUND);
        }

        ordersMapper.update(orders);
    }

    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orderQuery = Orders.builder().id(ordersCancelDTO.getId()).build();
        Orders order = ordersMapper.selectOne(orderQuery);

        if (order == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!Orders.CONFIRMED.equals(order.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        if (Orders.PAID.equals(order.getPayStatus())) {
            // TODO 退款的请求可以在此处补上
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason(ordersCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        ordersMapper.update(order);
    }

    @Override
    public void deliverOrder(Long id) {
        Orders orderQuery = Orders.builder().id(id).build();
        Orders order = ordersMapper.selectOne(orderQuery);

        if (order == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!Orders.CONFIRMED.equals(order.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        order.setDeliveryTime(LocalDateTime.now());
        ordersMapper.update(order);

        // 将延时任务加入消息队列
        OrderTleMessage orderTleMessage = OrderTleMessage.builder()
                .tleType(OrderTleMessage.FINISH_ORDER_TLE)
                .orderId(order.getId())
                .build();
        Message msg = MessageBuilder
                .withBody(JSON.toJSONString(orderTleMessage).getBytes(StandardCharsets.UTF_8))
                .setExpiration(OrderTleMessage.DELIVERY_FINISH_TIME_MS)
                .build();
        rabbitTemplate.convertAndSend("orderTleCancel.in.exchange", "114514", msg);
    }

    @Override
    public void completeOrder(Long id) {
        Orders orderQuery = Orders.builder().id(id).build();
        Orders order = ordersMapper.selectOne(orderQuery);

        if (order == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!Orders.DELIVERY_IN_PROGRESS.equals(order.getStatus()))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        order.setStatus(Orders.COMPLETED);
        ordersMapper.update(order);
    }

    @Override
    public void reminder(Long id) {
        JSONObject reminder = new JSONObject();
        reminder.put("type", Orders.NEW_ORDER);
        reminder.put("orderId", id);
        reminder.put("content", "客户催单：" + id);
        orderNotifyWs.sendToAllClient(reminder.toJSONString());
    }
}
