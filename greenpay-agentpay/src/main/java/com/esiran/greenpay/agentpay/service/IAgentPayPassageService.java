package com.esiran.greenpay.agentpay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageInputDTO;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;

import java.util.List;

/**
 * <p>
 * 代付通道 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface IAgentPayPassageService extends IService<AgentPayPassage> {
    List<AgentPayPassage> listByPayTypeCode(String payTypeCode);
    List<AgentPayPassageDTO> selectPage(IPage<AgentPayPassageDTO> page, AgentPayPassageDTO passageDTO);
    int add(AgentPayPassageInputDTO passageInputDTO) throws PostResourceException;
    boolean updateById(Integer id, AgentPayPassageInputDTO passageInputDTO) throws PostResourceException, ResourceNotFoundException;
    void delIds(List<Integer> ids) throws PostResourceException;

}
