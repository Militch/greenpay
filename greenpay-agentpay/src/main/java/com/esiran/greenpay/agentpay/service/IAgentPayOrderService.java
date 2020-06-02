package com.esiran.greenpay.agentpay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.pay.entity.OrderDTO;

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

}
