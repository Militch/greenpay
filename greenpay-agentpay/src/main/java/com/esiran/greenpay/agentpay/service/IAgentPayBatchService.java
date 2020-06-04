package com.esiran.greenpay.agentpay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentBatchInputVO;
import com.esiran.greenpay.agentpay.entity.AgentPayBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;

import java.util.List;

/**
 * <p>
 * 代付批次 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
public interface IAgentPayBatchService extends IService<AgentPayBatch> {

    List<AgentPayBatchDTO> selectPage(Page<AgentPayBatchDTO> page, AgentBatchInputVO agentBatchInputVO);
}
