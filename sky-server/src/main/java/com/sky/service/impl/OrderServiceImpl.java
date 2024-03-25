package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersPageQueryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
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
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
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
}
