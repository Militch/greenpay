package com.esiran.greenpay.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import com.esiran.greenpay.pay.entity.MchOrderQueryDTO;
import com.esiran.greenpay.pay.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;

import java.util.List;

/**
 * <p>
 * 支付订单 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
public interface IOrderService extends IService<Order> {
        IPage<OrderDTO> selectPage(IPage<OrderDTO> page,OrderDTO orderDTO);
        IPage<OrderDTO> selectPage(IPage<OrderDTO> page, OrderQueryDTO orderDTO);
        OrderDTO getByOrderNo(String orderNo);
        Order getOneByOrderNo(String orderNo);
        List<Order> getByDay(Integer mchId);
        void updateOrderStatus(String orderNo, Integer status);
        IPage<OrderDTO> findPageByMchId(IPage<OrderDTO> page, Integer mchId);
        IPage<OrderDTO> findPageByQuery(IPage<OrderDTO> page, Integer mchId, MchOrderQueryDTO queryDTO);


        List<Order> findIntradayOrder();


        //查询当天成功订单总数
        Integer findIntradayOrderSucc();
        //查询昨天成功订单总数
        Integer findYesterdayOrderSucc();

        //查询昨天0点到昨天当前时间总订单数
        Integer yestdayRealorderData();
        //查询今日0点到当前时间总订单数
        Integer intradayRealorderData();
        //查询昨天0点到昨天当前时间成交额
        Long yestdayRealmoneyData();
        //查询今日0点到当前时间成交额
        Long intradayRealoneyData();


        //七天数据统计
        List<CartogramDTO> sevenDayCartogram();

        List<CartogramDTO> upSevenDayCartogram();

        List<CartogramPayDTO> payOrders();

        List<CartogramDTO> hourData();

        List<CartogramDTO> hourData4amount();

        List<CartogramDTO> hourData4count();

        List<CartogramPayStatusVo> PayStatuss();
}
