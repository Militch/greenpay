package com.esiran.greenpay.agentpay.service;

import com.esiran.greenpay.agentpay.entity.PassageRisk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.agentpay.entity.PassageRiskDTO;
import com.esiran.greenpay.agentpay.entity.PassageRiskInputDTO;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Militch
 * @since 2020-06-05
 */
public interface IPassageRiskService extends IService<PassageRisk> {

    PassageRisk getByPassageId(Integer passageId);

    void updateRisk(Integer passageId, PassageRiskInputDTO inputDTO) throws ResourceNotFoundException, PostResourceException;
}
