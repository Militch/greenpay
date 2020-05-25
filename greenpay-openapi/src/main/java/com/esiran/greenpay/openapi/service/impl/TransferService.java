package com.esiran.greenpay.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayPassage;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.esiran.greenpay.agentpay.plugin.AgentPayOrderFlow;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageAccountService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.IdWorker;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.entity.MerchantAgentPayPassage;
import com.esiran.greenpay.merchant.entity.PrepaidAccount;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.openapi.entity.Transfer;
import com.esiran.greenpay.openapi.entity.TransferInputDTO;
import com.esiran.greenpay.openapi.service.ITransferService;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService implements ITransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final Gson gson = new GsonBuilder().create();
    private final IdWorker idWorker;
    private final IMerchantService merchantService;
    private final IAgentPayPassageService agentPayPassageService;
    private final IAgentPayPassageAccountService agentPayPassageAccountService;
    private final IInterfaceService interfaceService;
    private final IAgentPayOrderService orderService;
    private final PluginLoader pluginLoader;
    private final IPrepaidAccountService prepaidAccountService;
    public TransferService(
            IdWorker idWorker,
            IMerchantService merchantService,
            IAgentPayPassageService agentPayPassageService,
            IAgentPayPassageAccountService agentPayPassageAccountService,
            IInterfaceService interfaceService,
            IAgentPayOrderService orderService,
            PluginLoader pluginLoader,
            IPrepaidAccountService prepaidAccountService) {
        this.idWorker = idWorker;
        this.merchantService = merchantService;
        this.agentPayPassageService = agentPayPassageService;
        this.agentPayPassageAccountService = agentPayPassageAccountService;
        this.interfaceService = interfaceService;
        this.orderService = orderService;
        this.pluginLoader = pluginLoader;
        this.prepaidAccountService = prepaidAccountService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transfer createOneByInput(Integer mchId,TransferInputDTO inputDTO) throws APIException {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Merchant merchant = merchantService.getById(mchId);
        if (!merchant.getStatus()){
            throw new APIException("商户状态已锁定","MERCHANT_STATUS_LOCKED");
        }
        Transfer transfer = modelMapper.map(inputDTO, Transfer.class);
        transfer.setOrderNo(String.valueOf(idWorker.nextId()));
        transfer.setOrderSn(EncryptUtil.baseTimelineCode());
        transfer.setCreatedAt(LocalDateTime.now());
        transfer.setUpdatedAt(LocalDateTime.now());
        AgentPayOrder agentPayOrder = modelMapper.map(transfer, AgentPayOrder.class);
        MerchantAgentPayPassage mapp = merchantService.schedulerAgentPayPassage(mchId);
        if (mapp == null){
            throw new APIException("当前商户没有可用的通道","MERCHANT_NO_AVAILABLE_PASSAGE");
        }
        AgentPayPassage payPassage = agentPayPassageService.getById(mapp.getPassageId());
        if(payPassage == null||!payPassage.getStatus()){
            throw new APIException("通道不存在或未开启，无法创建订单","PASSAGE_NOT_SUPPORTED");
        }
        AgentPayPassageAccount account = agentPayPassageAccountService.schedulerAgentPayPassageAcc(payPassage.getId());
        if(account == null||!account.getStatus()){
            throw new APIException("通道账户不存在或未开启，无法创建订单","PASSAGE_ACC_NOT_SUPPORTED");
        }
        Interface ints = interfaceService.getByCode(payPassage.getInterfaceCode());
        if (ints == null || !ints.getStatus()){
            throw new APIException("支付接口不存在或未开启，无法创建订单","PAY_INTERFACE_NOT_SUPPORTED");
        }
        LambdaQueryWrapper<AgentPayOrder> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.eq(AgentPayOrder::getMchId,mchId);
        orderQueryWrapper.eq(AgentPayOrder::getOutOrderNo,inputDTO.getOutOrderNo());
        AgentPayOrder oldOrder = orderService.getOne(orderQueryWrapper);
        if (oldOrder != null) throw new APIException("支付接口不存在或未开启，无法创建订单","PAY_INTERFACE_NOT_SUPPORTED");
        // 订单支付渠道初始化
        agentPayOrder.setAgentpayPassageId(payPassage.getId());
        agentPayOrder.setAgentpayPassageName(payPassage.getPassageName());
        agentPayOrder.setAgentpayPassageAccId(account.getId());
        agentPayOrder.setPayInterfaceId(ints.getId());
        agentPayOrder.setPayTypeCode(ints.getPayTypeCode());
        agentPayOrder.setPayInterfaceAttr(account.getInterfaceAttr());
        // 订单金额以及手续费初始化
        Integer feeType = mapp.getFeeType();
        if (feeType == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
        Integer orderFee;
        Integer orderAmount = agentPayOrder.getAmount();
        if (feeType == 1){
            // 当手续费类型为百分比收费时，根据订单金额计算手续费
            BigDecimal feeRate = mapp.getFeeRate();
            if (feeRate == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
            orderFee = NumberUtil.calculateAmountFee(orderAmount,feeRate);
        }else if (feeType == 2){
            // 当手续费类型为固定收费时，手续费为固定金额
            Integer feeAmount = mapp.getFeeAmount();
            if (feeAmount == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
            orderFee = feeAmount;
        }else if(feeType == 3){
            // 当手续费类型为百分比加固定收费时，根据订单金额计算手续费然后加固定手续费
            BigDecimal feeRate = mapp.getFeeRate();
            Integer feeAmount = mapp.getFeeAmount();
            if (feeRate == null||feeAmount == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
            orderFee = NumberUtil.calculateAmountFee(orderAmount,feeRate);
            orderFee += feeAmount;
        }else {
            throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
        }
        PrepaidAccount pa = prepaidAccountService.getByMerchantId(mchId);
        if (pa == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
        int r = prepaidAccountService.updateBalance(mchId,(orderAmount+orderFee),-(orderAmount+orderFee));
        if (r <= 0){
            throw new APIException("账户可用余额不足，无法创建订单","ACCOUNT_AVAIL_BALANCE_NOT_ENOUGH",401);
        }
        agentPayOrder.setMchId(mchId);
        agentPayOrder.setFee(orderFee);
        agentPayOrder.setStatus(1);
        agentPayOrder.setCreatedAt(LocalDateTime.now());
        agentPayOrder.setUpdatedAt(LocalDateTime.now());
        orderService.save(agentPayOrder);
        AgentPayOrderFlow agentPayOrderFlow = new AgentPayOrderFlow(agentPayOrder);
        try {
            Plugin<AgentPayOrder> plugin = pluginLoader.loadForClassPath(ints.getInterfaceImpl());
            plugin.apply(agentPayOrderFlow);
            agentPayOrderFlow.execDependent("create");
        } catch (Exception e) {
            if (e instanceof APIException)
                throw new APIException(e.getMessage(),((APIException) e).getCode(),((APIException) e).getStatus());
            if (!StringUtils.isEmpty(e.getMessage())){
                throw new APIException(e.getMessage(),"CALL_AGENT_PAY_PASSAGE_ERROR",500);
            }
            throw new APIException("系统错误，调用代付通道接口执行失败","CALL_AGENT_PAY_PASSAGE_ERROR",500);
        }
//        Transfer out = modelMapper.map(agentPayOrder,Transfer.class);
        return modelMapper.map(agentPayOrder,Transfer.class);
    }
}
