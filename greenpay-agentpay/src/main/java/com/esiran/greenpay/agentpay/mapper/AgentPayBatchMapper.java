package com.esiran.greenpay.agentpay.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.agentpay.entity.AgentPayBatch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代付批次 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
public interface AgentPayBatchMapper extends BaseMapper<AgentPayBatch> {
    List<AgentPayBatch> agentPayBatchList(@Param(Constants.WRAPPER) Wrapper<AgentPayBatch> wrapper, Long page, Long size);
}
