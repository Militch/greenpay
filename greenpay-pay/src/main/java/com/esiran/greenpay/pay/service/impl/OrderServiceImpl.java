package com.esiran.greenpay.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.mapper.OrderMapper;
import com.esiran.greenpay.pay.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 支付订单 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    private final static ModelMapper modelMapper = new ModelMapper();



    @Override
    public IPage<OrderDTO> selectPage(IPage<OrderDTO> page, OrderDTO orderDTO) {
        LambdaQueryWrapper<Order> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.orderByDesc(Order::getCreatedAt);
        IPage<Order> orderPage = this.page(new Page<>(page.getCurrent(),page.getSize()),orderQueryWrapper);
        return orderPage.convert(OrderDTO::convertOrderEntity);
    }

    @Override
    public IPage<OrderDTO> selectPage(IPage<OrderDTO> page, OrderQueryDTO orderDTO) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Order order = modelMapper.map(orderDTO,Order.class);
        LambdaQueryWrapper<Order> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.orderByDesc(Order::getCreatedAt);
        if (orderDTO.getStartTime() != null){
            orderQueryWrapper.ge(Order::getCreatedAt,orderDTO.getStartTime());
        }
        if (orderDTO.getEndTime() != null){
            orderQueryWrapper.lt(Order::getCreatedAt,orderDTO.getEndTime());
        }
        orderQueryWrapper.setEntity(order);
        IPage<Order> orderPage = this.page(new Page<>(page.getCurrent(),page.getSize()),orderQueryWrapper);
        return orderPage.convert(OrderDTO::convertOrderEntity);
    }


    @Override
    public OrderDTO getByOrderNo(String orderNo) {
        Order order = this.getOneByOrderNo(orderNo);
        if (order == null) return null;
        return OrderDTO.convertOrderEntity(order);
    }

    @Override
    public Order getOneByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Order::getOrderNo, orderNo);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public List<Order> getByDay(Integer mchId) {
        return this.baseMapper.getByDay(mchId);
    }

    @Override
    public void updateOrderStatus(String orderNo, Integer status) {
        if (orderNo == null || status == null) return;
        LambdaUpdateWrapper<Order> orderUpdateWrapper = new LambdaUpdateWrapper<>();
        orderUpdateWrapper.set(Order::getStatus, status)
                .eq(Order::getOrderNo,orderNo);
        update(orderUpdateWrapper);
    }

    @Override
    public IPage<OrderDTO> findPageByMchId(IPage<OrderDTO> page, Integer mchId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreatedAt);
        wrapper.eq(Order::getMchId,mchId);
        IPage<Order> orderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return orderPage.convert(OrderDTO::convertOrderEntity);
    }

    @Override
    public IPage<OrderDTO> findPageByQuery(IPage<OrderDTO> page, Integer mchId, MchOrderQueryDTO queryDTO) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreatedAt);
        wrapper.eq(Order::getMchId,mchId);
        if (!StringUtils.isEmpty(queryDTO.getOrderNo())){
            wrapper.eq(Order::getOrderNo,queryDTO.getOrderNo());
        }
        if (!StringUtils.isEmpty(queryDTO.getOutOrderNo())){
            wrapper.eq(Order::getOutOrderNo,queryDTO.getOutOrderNo());
        }
        if (!StringUtils.isEmpty(queryDTO.getStatus())){
            wrapper.eq(Order::getStatus,queryDTO.getStatus());
        }
        if (!StringUtils.isEmpty(queryDTO.getStartTime())){
            wrapper.ge(Order::getCreatedAt,queryDTO.getStartTime());
        }
        if (!StringUtils.isEmpty(queryDTO.getEndTime())){
            wrapper.lt(Order::getCreatedAt,queryDTO.getEndTime());
        }
        IPage<Order> orderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return orderPage.convert(OrderDTO::convertOrderEntity);
    }

    @Override
    public List<Order> findIntradayOrder() {
        return   baseMapper.findIntradayOrder(new QueryWrapper<>());
    }

//    //查询昨天0点到昨天当前时间总订单数
//    @Override
//    public Integer yestdayRealorderData() {
//        return this.baseMapper.yestdayRealorderData();
//    }
//    //查询今日0点到当前时间总订单数
//    @Override
//    public Integer intradayRealorderData() {
//        return this.baseMapper.intradayRealorderData();
//    }
//    //查询昨天0点到昨天当前时间成交额
//    @Override
//    public Long yestdayRealmoneyData() {
//        return this.baseMapper.yestdayRealmoneyData();
//    }
//    //查询今日0点到当前时间成交额
//    @Override
//    public Long intradayRealoneyData() {
//        return this.baseMapper.intradayRealmoneyData();
//    }

    @Override
    public CartogramDTO findIntradayOrderAll() {
        return this.baseMapper.findIntradayOrderAll();
    }

    @Override
    public CartogramDTO findYesterdayOrderAll() {
        return this.baseMapper.findYesterdayOrderAll();
    }

    @Override
    public List<CartogramDTO> sevenDayAllCount() {
        return this.baseMapper.sevenDayAllCount();
    }

    @Override
    public List<CartogramDTO> sevenDayAllAmount() {
        return this.baseMapper.sevenDayAllAmount();
    }

    @Override
    public List<CartogramDTO> upSevenDayCartogram() {
        return this.baseMapper.upSevenDayAllCount();
    }

    @Override
    public List<CartogramDTO> upSevenDayAllAmount() {
        return this.baseMapper.upSevenDayAllAmount();
    }

    @Override
    public List<CartogramPayDTO> payOrders() {
        return this.baseMapper.payOrders();
    }

    @Override
    public List<CartogramDTO> hourData() {
        return this.baseMapper.hourAllData();
    }

    @Override
    public List<CartogramDTO> hourData4amount() {
        return this.baseMapper.hourData4amount();
    }

    @Override
    public List<CartogramDTO> hourData4count() {
        return this.baseMapper.hourData4count();
    }

    @Override
    public List<CartogramPayStatusVo> PayStatuss() {
        return this.baseMapper.PayStatuss();
    }


    @Override
    public List<CartogramDTO> sevenDay4CountAndAmount() {
        return this.baseMapper.sevenDay4CountAndAmount();
    }

    @Override
    public List<CartogramDTO> currentMonth4CountAndAmount() {
        return this.baseMapper.currentMonth4CountAndAmount();
    }
}
