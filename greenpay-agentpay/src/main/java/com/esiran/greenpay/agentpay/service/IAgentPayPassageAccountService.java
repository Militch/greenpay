package com.esiran.greenpay.agentpay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccountDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccountInputDTO;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;

import java.util.List;

/**
 * <p>
 * 代付通道账户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
public interface IAgentPayPassageAccountService extends IService<AgentPayPassageAccount> {
    List<AgentPayPassageAccount> listByPayTypeCode(String payTypeCode);
    IPage<AgentPayPassageAccountDTO> selectPage(IPage<AgentPayPassageAccountDTO> page, AgentPayPassageAccountDTO passageAccountDTO);
    int add(AgentPayPassageAccountInputDTO passageAccountInputDTO) throws PostResourceException;
    boolean updateById(Integer id, AgentPayPassageAccountInputDTO passageAccountInputDTO) throws PostResourceException, ResourceNotFoundException;
    void delByIds(List<Integer> ids) throws PostResourceException;
    List<AgentPayPassageAccount> listAvailableByPassageId(Integer passageId);
    List<AgentPayPassageAccount> filterAppa(List<AgentPayPassageAccount> accounts,Integer orderAmount);
    Integer getAppaBalance(AgentPayPassageAccount appa);
    AgentPayPassageAccount schedulerAgentPayPassageAcc(Integer passageId);
    AgentPayPassageAccount schedulerAgentPayPassageAcc(Integer passageId, Integer orderAmount);
}
