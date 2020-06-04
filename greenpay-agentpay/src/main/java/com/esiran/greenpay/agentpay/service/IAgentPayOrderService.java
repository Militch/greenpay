package com.esiran.greenpay.agentpay.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderInputVO;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDTO;

import java.util.List;

/**
 * <p>
 * 代付订单表 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface IAgentPayOrderService extends IService<AgentPayOrder> {

    IPage<AgentPayOrderDTO> selectPage(Page<AgentPayOrderDTO> page, AgentPayOrderDTO agentPayOrderDTO);

    AgentPayOrderDTO getbyOrderNo(String orderNo);

    AgentPayOrder getOneByOrderNo(String orderNo);

//    AgentPayOrder
    String createOneBatchOrder(AgentPayOrder agentPayOrder) throws APIException;

    List<AgentPayOrderDTO> agentPayOrderList(Page<AgentPayOrderDTO> page, AgentPayOrderInputVO agentPayOrderInputVO);

    List<AgentPayOrderDTO> findIntradayOrdersByMchId(Integer  mchId);

    List<AgentPayOrderDTO> findYesterdayOrdersByMchId(Integer  mchId);


    List<AgentPayOrderDTO> findIntradayOrders();

    List<AgentPayOrderDTO> findYesterdayOrders();

    //24小时数据
    List<CartogramDTO> hourAllData();

    //7日数据
    List<CartogramDTO> sevenDayAllData();

    //上周的数据
    List<CartogramDTO> upWeekAllData();

    //一周的数据
    List<CartogramDTO> sevenDay4CountAndAmount();

    //本月的数据
    List<CartogramDTO> currentMonth4CountAndAmount();

    //支付排行
    List<CartogramPayDTO> payRanking();

    //转化率
    List<CartogramPayStatusVo> payCRV();


}
