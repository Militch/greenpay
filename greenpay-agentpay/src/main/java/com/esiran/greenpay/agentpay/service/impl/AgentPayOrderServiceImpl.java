package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderInputVO;
import com.esiran.greenpay.agentpay.mapper.AgentPayOrderMapper;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.pay.entity.CartogramDTO;
import com.esiran.greenpay.pay.entity.CartogramPayDTO;
import com.esiran.greenpay.pay.entity.CartogramPayStatusVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 代付订单表 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Service
public class AgentPayOrderServiceImpl extends ServiceImpl<AgentPayOrderMapper, AgentPayOrder> implements IAgentPayOrderService {

    @Override
    public IPage<AgentPayOrderDTO> selectPage(Page<AgentPayOrderDTO> page, AgentPayOrderDTO agentPayOrderDTO) {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AgentPayOrder::getCreatedAt);
        Page<AgentPayOrder> orderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return orderPage.convert(AgentPayOrderDTO::convertOrderEntity);
    }

    @Override
    public List<AgentPayOrderDTO> agentPayOrderList(Page<AgentPayOrderDTO> page , AgentPayOrderInputVO agentPayOrderInputVO) {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(agentPayOrderInputVO.getOrderNo())) {
            wrapper.eq(AgentPayOrder::getOrderNo, agentPayOrderInputVO.getOrderNo());
        }
        if (agentPayOrderInputVO.getStatus() != null && agentPayOrderInputVO.getStatus() != 0) {
            wrapper.eq(AgentPayOrder::getStatus, agentPayOrderInputVO.getStatus());
        }
        if (!StringUtils.isEmpty(agentPayOrderInputVO.getStartTime() )) {
            wrapper.ge(AgentPayOrder::getCreatedAt, agentPayOrderInputVO.getStartTime());
        }

        if (!StringUtils.isEmpty(agentPayOrderInputVO.getEndTime() )) {
            wrapper.lt(AgentPayOrder::getCreatedAt, agentPayOrderInputVO.getEndTime());
        }
        List<AgentPayOrder> agentPayOrder = this.baseMapper.agentPayOrderList(wrapper,((page.getCurrent()-1) * page.getSize()),page.getSize());
        List<AgentPayOrderDTO> agentPayOrderDTOStream = agentPayOrder.stream().map(AgentPayOrderDTO::convertOrderEntity).collect(Collectors.toList());

        return agentPayOrderDTOStream;
    }

    @Override
    public AgentPayOrderDTO getbyOrderNo(String orderNo) {
        AgentPayOrder oneByOrderNo = this.getOneByOrderNo(orderNo);
        if (oneByOrderNo == null ) return null;
        return AgentPayOrderDTO.convertOrderEntity(oneByOrderNo);
    }

    @Override
    public AgentPayOrder getOneByOrderNo(String orderNo) {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentPayOrder::getOrderNo,orderNo);
        return this.getOne(wrapper);
    }




    @Override
    public List<AgentPayOrderDTO> findIntradayOrdersByMchId(Integer mchId) {
        List<AgentPayOrderDTO> intradayOrders = this.baseMapper.findIntradayOrdersByMchId(mchId);
        return intradayOrders;
    }

    @Override
    public List<AgentPayOrderDTO> findYesterdayOrdersByMchId(Integer mchId) {
        List<AgentPayOrderDTO> yesterdayOrders = this.baseMapper.findYesterdayOrdersByMchId(mchId);
        return yesterdayOrders;
    }

    @Override
    public List<AgentPayOrderDTO> findIntradayOrders() {
        List<AgentPayOrderDTO> intradayOrders = this.baseMapper.findIntradayOrders();
        return intradayOrders;
    }

    @Override
    public List<AgentPayOrderDTO> findYesterdayOrders() {
        List<AgentPayOrderDTO> yesterdayOrders = this.baseMapper.findYesterdayOrders();
        return yesterdayOrders;
    }


    @Override
    public List<CartogramDTO> hourAllData() {
        return this.baseMapper.hourAllData();
    }

    @Override
    public List<CartogramDTO> sevenDayAllData() {
        return this.baseMapper.sevenDayAllData();
    }

    @Override
    public List<CartogramDTO> upWeekAllData() {
        return this.baseMapper.upWeekAllData();
    }

    @Override
    public List<CartogramDTO> sevenDay4CountAndAmount() {
        return this.baseMapper.sevenDay4CountAndAmount();
    }

    @Override
    public List<CartogramDTO> currentMonth4CountAndAmount() {
        return this.baseMapper.currentMonth4CountAndAmount();
    }

    @Override
    public List<CartogramPayDTO> payRanking() {
        return this.baseMapper.payRanking();
    }

    @Override
    public List<CartogramPayStatusVo> payCRV() {
        return this.baseMapper.payCRV();
    }
}
