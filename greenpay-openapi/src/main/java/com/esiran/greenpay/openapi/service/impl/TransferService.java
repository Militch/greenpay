package com.esiran.greenpay.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.agentpay.entity.*;
import com.esiran.greenpay.agentpay.plugin.AgentPayOrderFlow;
import com.esiran.greenpay.agentpay.service.*;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.IdWorker;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.entity.MerchantAgentPayPassage;
import com.esiran.greenpay.merchant.entity.PrepaidAccount;
import com.esiran.greenpay.merchant.entity.PrepaidAccountDTO;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
import com.esiran.greenpay.openapi.entity.*;
import com.esiran.greenpay.openapi.service.ITransferService;
import com.esiran.greenpay.pay.entity.Interface;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransferService implements ITransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final Gson gson = new GsonBuilder().create();
    private final IdWorker idWorker;
    private final IMerchantService merchantService;
    private final IAgentPayBatchService agentPayBatchService;
    private final IAgentPayPassageService agentPayPassageService;
    private final IAgentPayPassageAccountService agentPayPassageAccountService;
    private final IInterfaceService interfaceService;
    private final IAgentPayOrderService orderService;
    private final PluginLoader pluginLoader;
    private final IPrepaidAccountService prepaidAccountService;
    private final RedisDelayQueueClient redisDelayQueueClient;
    private final IPassageRiskService passageRiskService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public TransferService(
            IdWorker idWorker,
            IMerchantService merchantService,
            IAgentPayBatchService agentPayBatchService, IAgentPayPassageService agentPayPassageService,
            IAgentPayPassageAccountService agentPayPassageAccountService,
            IInterfaceService interfaceService,
            IAgentPayOrderService orderService,
            PluginLoader pluginLoader,
            IPrepaidAccountService prepaidAccountService, RedisDelayQueueClient redisDelayQueueClient,
            IPassageRiskService passageRiskService, KafkaTemplate<String, String> kafkaTemplate) {
        this.idWorker = idWorker;
        this.merchantService = merchantService;
        this.agentPayBatchService = agentPayBatchService;
        this.agentPayPassageService = agentPayPassageService;
        this.agentPayPassageAccountService = agentPayPassageAccountService;
        this.interfaceService = interfaceService;
        this.orderService = orderService;
        this.pluginLoader = pluginLoader;
        this.prepaidAccountService = prepaidAccountService;
        this.redisDelayQueueClient = redisDelayQueueClient;
        this.passageRiskService = passageRiskService;
        this.kafkaTemplate = kafkaTemplate;
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
        PassageRisk passageRisk = passageRiskService.getByPassageId(payPassage.getId());
        if (passageRisk != null && passageRisk.getStatus() == 1){
            if (agentPayOrder.getAmount() < passageRisk.getAmountMin()){
                throw new APIException(String.format("订单金额不得低于%s元",NumberUtil.amountFen2Yuan(passageRisk.getAmountMin())),"PASSAGE_RISK");
            }
            if (agentPayOrder.getAmount() > passageRisk.getAmountMax()){
                throw new APIException(String.format("订单金额不得高于%s元",NumberUtil.amountFen2Yuan(passageRisk.getAmountMax())),"PASSAGE_RISK");
            }
        }

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
        AgentPayPassageAccount account = agentPayPassageAccountService.schedulerAgentPayPassageAcc(payPassage.getId(),orderAmount+orderFee);
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
        AgentPayOrder order = null;
        try {
            String impl = ints.getInterfaceImpl();
            Pattern pattern = Pattern.compile("(^|;)corder:(.+?)(;|$)");
            Matcher m = pattern.matcher(impl);
            if (!m.find())
                throw new IllegalAccessException("接口调用失败");
            Plugin<AgentPayOrder> plugin = pluginLoader.loadForClassPath(m.group(2));
            plugin.apply(agentPayOrderFlow);
            agentPayOrderFlow.execDependent("create");
            Map<String, Object> results = agentPayOrderFlow.getResults();
            String status = (String) results.get("status");
            LambdaUpdateWrapper<AgentPayOrder> updateWrapperwrapper = new LambdaUpdateWrapper<>();
            if (status.equals("30")){
                updateWrapperwrapper.set(AgentPayOrder::getStatus,-1)
                        .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                        .eq(AgentPayOrder::getId,agentPayOrder.getId());
                orderService.update(updateWrapperwrapper);
                prepaidAccountService.updateBalance(mchId,0,(orderAmount+orderFee));
            }
            if (status.equals("20")){
                updateWrapperwrapper.set(AgentPayOrder::getStatus,3)
                        .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                        .eq(AgentPayOrder::getId,agentPayOrder.getId());
                orderService.update(updateWrapperwrapper);
                prepaidAccountService.updateBalance(mchId,0,(orderAmount+orderFee));
            }
            if (status.equals("40")) {
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("orderNo", agentPayOrder.getOrderNo());
                queryMap.put("count", "1");
                String queryMsg = gson.toJson(queryMap);
                redisDelayQueueClient.sendDelayMessage("agentpay:query", queryMsg, 0);
            }
            prepaidAccountService.updateBalance(mchId,0,(orderAmount+orderFee));
            order = orderService.getOneByOrderNo(agentPayOrder.getOrderNo());
            return modelMapper.map(order,Transfer.class);
        } catch (Exception e) {
            if (e instanceof APIException)
                throw new APIException(e.getMessage(),((APIException) e).getCode(),((APIException) e).getStatus());
            if (!StringUtils.isEmpty(e.getMessage())){
                throw new APIException(e.getMessage(),"CALL_AGENT_PAY_PASSAGE_ERROR",500);
            }
            assert order != null;
            return modelMapper.map(order,Transfer.class);
        }
    }

    private void checkBatchOneOrder(BatchOrder batchOrder) throws APIException {
        if (StringUtils.isEmpty(batchOrder.getOutOrderNo())){
            batchOrder.setOutOrderNo(String.valueOf(idWorker.nextId()));
        }else if (batchOrder.getOutOrderNo().length() > 20){
            throw new APIException(String.format("扩展参数订单号%s: 异常",batchOrder.getOutOrderNo()),"");
        } else if (StringUtils.isEmpty(batchOrder.getAccountName())){
            throw new APIException(String.format("扩展参数订单号%s: 账户名异常",batchOrder.getOutOrderNo()),"");
        }else if (StringUtils.isEmpty(batchOrder.getAccountType())){
            throw new APIException(String.format("扩展参数订单号%s: 账户类型异常",batchOrder.getOutOrderNo()),"");
        }else if (StringUtils.isEmpty(batchOrder.getAmount()) || batchOrder.getAmount() <= 0){
            throw new APIException(String.format("扩展参数订单号%s: 订单金额异常",batchOrder.getOutOrderNo()),"");
        }else if (StringUtils.isEmpty(batchOrder.getAccountNumber())){
            throw new APIException(String.format("扩展参数订单号%s: 账户号异常",batchOrder.getOutOrderNo()),"");
        }else if (StringUtils.isEmpty(batchOrder.getBankName())){
            throw new APIException(String.format("扩展参数订单号%s: 开户行异常",batchOrder.getOutOrderNo()),"");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchRes batch(BatchInputDTO batchInputDTO, Integer mchId) throws APIException {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        String recipients = batchInputDTO.getRecipients();
        List<BatchOrder> list;
        try {
            list = gson.fromJson(recipients, new TypeToken<List<BatchOrder>>(){}.getType());
        }catch (Exception e){
            throw new APIException("收款对象列表格式错误","RECIPIENTS_FORMAT_ERROR");
        }
        if (batchInputDTO.getTotalCount() != list.size()){
            throw new APIException("代付总笔数与实际总笔数不符","");
        }
        int totalAmount = 0;
        for (BatchOrder batchOrder : list) {
            checkBatchOneOrder(batchOrder);
            totalAmount += batchOrder.getAmount();
        }
        if (batchInputDTO.getTotalAmount() != totalAmount){
            throw new APIException("代付总金额与实际总金额不符","");
        }
        MerchantAgentPayPassage mapp = merchantService.schedulerAgentPayPassage(mchId);
        if (mapp == null){
            throw new APIException("当前商户没有可用的通道","MERCHANT_NO_AVAILABLE_PASSAGE");
        }
        AgentPayPassage payPassage = agentPayPassageService.getById(mapp.getPassageId());
        if(payPassage == null||!payPassage.getStatus()){
            throw new APIException("通道不存在或未开启，无法创建订单","PASSAGE_NOT_SUPPORTED");
        }
        Integer orderFee = OrderFee(mapp, totalAmount, batchInputDTO.getTotalCount());
        int r = prepaidAccountService.updateBalance(mchId,(totalAmount+orderFee),-(totalAmount+orderFee));
        if (r <= 0){
            throw new APIException("账户可用余额不足，无法创建订单","ACCOUNT_AVAIL_BALANCE_NOT_ENOUGH",401);
        }
        AgentPayPassageAccount account = agentPayPassageAccountService.schedulerAgentPayPassageAcc(payPassage.getId(),totalAmount+orderFee);
        if(account == null||!account.getStatus()){
            throw new APIException("通道账户不存在或未开启，无法创建订单","PASSAGE_ACC_NOT_SUPPORTED");
        }
        Interface ints = interfaceService.getByCode(payPassage.getInterfaceCode());
        if (ints == null || !ints.getStatus()){
            throw new APIException("支付接口不存在或未开启，无法创建订单","PAY_INTERFACE_NOT_SUPPORTED");
        }
        AgentPayBatch agentPayBatch = modelMapper.map(batchInputDTO, AgentPayBatch.class);
        agentPayBatch.setBatchNo(String.valueOf(idWorker.nextId()));
        agentPayBatch.setMchId(mchId);
        agentPayBatch.setPayTypeCode(ints.getPayTypeCode());
        agentPayBatch.setAgentpayPassageId(payPassage.getId());
        agentPayBatch.setAgentpayPassageAccId(account.getId());
        agentPayBatch.setPayInterfaceId(ints.getId());
        agentPayBatch.setStatus(1);
        agentPayBatchService.save(agentPayBatch);
        for (BatchOrder batchOrder : list) {
            AgentPayOrder agentPayOrder = modelMapper.map(batchOrder,AgentPayOrder.class);
            agentPayOrder.setPayTypeCode(ints.getPayTypeCode());
            Integer fee = OrderFee(mapp, totalAmount, batchInputDTO.getTotalCount());
            agentPayOrder.setFee(fee);
            agentPayOrder.setOrderNo(String.valueOf(idWorker.nextId()));
            agentPayOrder.setOrderSn(String.valueOf(idWorker.nextId()));
            agentPayOrder.setMchId(mchId);
            agentPayOrder.setAgentpayPassageId(payPassage.getId());
            agentPayOrder.setAgentpayPassageName(payPassage.getPassageName());
            agentPayOrder.setAgentpayPassageAccId(account.getId());
            agentPayOrder.setPayInterfaceId(ints.getId());
            agentPayOrder.setPayInterfaceAttr(account.getInterfaceAttr());
            agentPayOrder.setBatchNo(agentPayBatch.getBatchNo());
            kafkaTemplate.send("grennpay_agentpay_batch_order_create",gson.toJson(agentPayOrder));
        }
        BatchRes aReturn = modelMapper.map(agentPayBatch, BatchRes.class);
        return aReturn;
    }

    @Override
    public String queryAmount(Integer mchId) throws APIException {
        PrepaidAccountDTO prepaidAccount = prepaidAccountService.findByMerchantId(mchId);
        Map<String,String> result = new HashMap<>();
        if (prepaidAccount == null ){
            throw new APIException("商户不存在","");
        }else {
            result.put("availBalance",prepaidAccount.getAvailBalanceDisplay());
            result.put("freezeBalance",prepaidAccount.getFreezeBalanceDisplay());
        }
        return gson.toJson(result);
    }

    @Override
    public AgentPayRes queryAgentPay(String outOrderNo, String orderNo) throws APIException {
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isEmpty(outOrderNo)){
            wrapper.eq(AgentPayOrder::getOrderNo,orderNo);
        }
        if (StringUtils.isEmpty(orderNo)){
            wrapper.eq(AgentPayOrder::getOutOrderNo,outOrderNo);
        }
        if(!StringUtils.isEmpty(outOrderNo) && !StringUtils.isEmpty(orderNo)){
            wrapper.eq(AgentPayOrder::getOrderNo,orderNo)
                    .eq(AgentPayOrder::getOutOrderNo,outOrderNo);
        }
        AgentPayOrder agentPayOrder = orderService.getOne(wrapper);
        if (agentPayOrder != null){
            AgentPayRes agentPayRes = modelMapper.map(agentPayOrder, AgentPayRes.class);
            return agentPayRes;
        }
        throw new APIException("订单不存在","");
    }

    @Override
    public BatchRes queryBatch(String outBatchNo, String batchNo) throws APIException {
        LambdaQueryWrapper<AgentPayBatch> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isEmpty(outBatchNo)){
            wrapper.eq(AgentPayBatch::getBatchNo,batchNo);
        }
        if (StringUtils.isEmpty(batchNo)){
            wrapper.eq(AgentPayBatch::getOutBatchNo,outBatchNo);
        }
        if(!StringUtils.isEmpty(batchNo) &&  !StringUtils.isEmpty(outBatchNo)){
            wrapper.eq(AgentPayBatch::getBatchNo,batchNo)
                    .eq(AgentPayBatch::getOutBatchNo,outBatchNo);
        }
        AgentPayBatch payBatch = agentPayBatchService.getOne(wrapper);
        if (payBatch != null){
            BatchRes batchRes = modelMapper.map(payBatch, BatchRes.class);
            return batchRes;
        }
        throw new APIException("订单不存在","");
    }

    public Integer OrderFee(MerchantAgentPayPassage mapp, Integer amount,Integer count) throws APIException {
        Integer feeType = mapp.getFeeType();
        if (feeType == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
        Integer orderFee;
        if (feeType == 1){
            // 当手续费类型为百分比收费时，根据订单金额计算手续费
            BigDecimal feeRate = mapp.getFeeRate();
            if (feeRate == null) throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
            orderFee = NumberUtil.calculateAmountFee(amount,feeRate);
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
            orderFee = NumberUtil.calculateAmountFee(amount,feeRate);
            orderFee += feeAmount * count;
        }else {
            throw new APIException("系统错误，无法创建订单","SYSTEM_ERROR",500);
        }
        return orderFee;
    }

}
