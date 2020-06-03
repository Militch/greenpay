package com.esiran.greenpay.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.merchant.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.pay.entity.PassageAndSubAccount;
import com.esiran.greenpay.pay.entity.Product;
import io.swagger.models.auth.In;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IMerchantService extends IService<Merchant> {
    void updateMerchantInfoById(MerchantUpdateDTO dto, Integer id) throws Exception;
    void updateMerchantProduct(MerchantProductInputDTO dto, Integer id) throws Exception;
    void updatePasswordById(String password, Integer id) throws Exception;
    void updateSettleById(SettleAccountDTO settleAccountDTO, Integer id) throws Exception;
    void updateAccountBalance(Integer accType, Integer mchId, Double amount,  Integer type, Integer action) throws Exception;
    MerchantDetailDTO findMerchantById(Integer id);
    void addMerchant(MerchantInputDTO merchantInputDTO) throws Exception;
    List<MerchantProductDTO> selectMchProductById(Integer mchId) throws APIException, ResourceNotFoundException;
    List<MerchantAgentPayPassageDTO> listMchAgentPayPassageByMchId(Integer mchId);
    MerchantAgentPayPassageDTO selectMchAgentPayPassageByMchId(Integer mchId, Integer passageId);
    MerchantProductDTO selectMchProductById(Integer mchId,Integer productId);
    IPage<MerchantDTO> selectMerchantByPage(IPage<Void> page);
    PassageAndSubAccount scheduler(Integer mchId, Integer productId);
    MerchantAgentPayPassage schedulerAgentPayPassage(Integer mchId);
    AgentPayPassageAccount schedulerAgentPayPassageAcc(Integer mchId, Integer passageId);
    HashMap<String,Object> homeData(Integer mchId);

    Boolean delMerchant(Integer mchid) throws PostResourceException;




}
