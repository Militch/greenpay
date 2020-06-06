package com.esiran.greenpay.agentpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentBatchInputVO;
import com.esiran.greenpay.agentpay.entity.AgentPayBatch;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.mapper.AgentPayBatchMapper;
import com.esiran.greenpay.agentpay.service.IAgentPayBatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 代付批次 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Service
public class AgentPayBatchServiceImpl extends ServiceImpl<AgentPayBatchMapper, AgentPayBatch> implements IAgentPayBatchService {

//    @Override
//    public IPage<AgentPayBatchDTO> selectPage(Page<AgentPayBatchDTO> page, AgentBatchInputVO agentBatchInputVO) {
//        LambdaQueryWrapper<AgentPayBatch> wrapper = new LambdaQueryWrapper<>();
//        wrapper.orderByDesc(AgentPayBatch::getCreatedAt);
//        Page<AgentPayBatch> batchPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
//        return batchPage.convert(AgentPayBatchDTO::convertOrderEntity);
//    }

    @Override
    public List<AgentPayBatchDTO> selectPage(Page<AgentPayBatchDTO> page, AgentBatchInputVO agentBatchInputVO) {
        LambdaQueryWrapper<AgentPayBatch> wrapper = new LambdaQueryWrapper<>();
        if (agentBatchInputVO != null) {
            if (!StringUtils.isEmpty(agentBatchInputVO.getMchId()) && agentBatchInputVO.getMchId()>0){
                wrapper.eq(AgentPayBatch::getMchId, agentBatchInputVO.getMchId());
            }
            if (!StringUtils.isEmpty(agentBatchInputVO.getBatchNo())) {
                wrapper.eq(AgentPayBatch::getBatchNo, agentBatchInputVO.getBatchNo());
            }
            if (agentBatchInputVO.getStatus() != null && agentBatchInputVO.getStatus() != 0) {
                wrapper.eq(AgentPayBatch::getStatus, agentBatchInputVO.getStatus());
            }
            if (!StringUtils.isEmpty(agentBatchInputVO.getStartTime() )) {
                wrapper.ge(AgentPayBatch::getCreatedAt, agentBatchInputVO.getStartTime());
            }

            if (!StringUtils.isEmpty(agentBatchInputVO.getEndTime() )) {
                wrapper.lt(AgentPayBatch::getCreatedAt, agentBatchInputVO.getEndTime());
            }
        }

        List<AgentPayBatch> agentPayOrder = this.baseMapper.agentPayBatchList(wrapper,((page.getCurrent()-1) * page.getSize()),page.getSize());
        List<AgentPayBatchDTO> collect = agentPayOrder.stream().map(AgentPayBatchDTO::convertOrderEntity).collect(Collectors.toList());

        return collect;
    }
}
