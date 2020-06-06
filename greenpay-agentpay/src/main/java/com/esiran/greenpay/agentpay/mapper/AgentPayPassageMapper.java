package com.esiran.greenpay.agentpay.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.agentpay.entity.AgentPayBatch;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代付通道 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface AgentPayPassageMapper extends BaseMapper<AgentPayPassage> {
    List<AgentPayPassage> agentPayPassageList(@Param(Constants.WRAPPER) Wrapper<AgentPayPassage> wrapper, Long page, Long size);
}
