package com.esiran.greenpay.admin.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.bank.pingan.api.PingAnApiEx;
import com.esiran.greenpay.bank.pingan.entity.HeaderMsg;
import com.esiran.greenpay.bank.pingan.entity.QueryOnceAgentPay;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.message.delayqueue.DelayQueueTaskRunner;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class AgentPayQueryTaskRunner implements DelayQueueTaskRunner {
    private final Gson g = new Gson();
    private final IAgentPayOrderService agentPayOrderService;
    private final IPrepaidAccountService prepaidAccountService;
    private final RedisDelayQueueClient redisDelayQueueClient;
    public AgentPayQueryTaskRunner(IAgentPayOrderService agentPayOrderService, IPrepaidAccountService prepaidAccountService, RedisDelayQueueClient redisDelayQueueClient) {
        this.agentPayOrderService = agentPayOrderService;
        this.prepaidAccountService = prepaidAccountService;
        this.redisDelayQueueClient = redisDelayQueueClient;
    }

    @Override
    public void exec(String content) {
        Map<String, String> queryMap = MapUtil.jsonString2stringMap(content);
        assert queryMap != null;
        String orderNo = queryMap.get("orderNo");
        String count = queryMap.get("count");
        LambdaQueryWrapper<AgentPayOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentPayOrder::getOrderNo,orderNo);
        AgentPayOrder agentPayOrder = agentPayOrderService.getOne(wrapper);
        if (agentPayOrder == null || agentPayOrder.getStatus() != 2){
            return;
        }
        String attr = agentPayOrder.getPayInterfaceAttr();
        Map<String, String> attrmap = MapUtil.jsonString2stringMap(attr);
        if (attrmap == null
                || attrmap.get("companyCode") == null
                || attrmap.get("acctNo") == null
                || attrmap.get("host") == null){
            return;
        }
        HeaderMsg headerMsg = new HeaderMsg();
        headerMsg.setOutOrderNumber(agentPayOrder.getOrderSn());
        headerMsg.setCompanyCode(attrmap.get("companyCode"));
        PingAnApiEx apiEx = new PingAnApiEx(attrmap.get("host"), headerMsg);
        QueryOnceAgentPay queryOnceAgentPay = new QueryOnceAgentPay();
        queryOnceAgentPay.setOrderNumber(orderNo);
        queryOnceAgentPay.setAcctNo(attrmap.get("acctNo"));
        try {
            Map<String, String> map = apiEx.queryOnceAgentPay(queryOnceAgentPay);
            if (map != null){
                if (map.get("Status").equals("30")){
                    LambdaUpdateWrapper<AgentPayOrder> updateWrapperwrapper = new LambdaUpdateWrapper<>();
                    updateWrapperwrapper.set(AgentPayOrder::getStatus,-1)
                            .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                            .eq(AgentPayOrder::getId,agentPayOrder.getId());
                    agentPayOrderService.update(updateWrapperwrapper);
                }
                if (map.get("Status").equals("20")){
                    LambdaUpdateWrapper<AgentPayOrder> updateWrapperwrapper = new LambdaUpdateWrapper<>();
                    updateWrapperwrapper.set(AgentPayOrder::getStatus,3)
                            .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                            .eq(AgentPayOrder::getId,agentPayOrder.getId());
                    agentPayOrderService.update(updateWrapperwrapper);
                }
                if (!(map.get("Status").equals("30") && map.get("Status").equals("20"))){
                    int i = Integer.parseInt(count);
                    if (i < 5){
                        queryMap.put("count",String.valueOf(i+1));
                        String queryMsg = g.toJson(queryMap);
                        redisDelayQueueClient.sendDelayMessage("agentpay:query",queryMsg,30 * 1000);
                    }
                }
            }
        } catch (Exception e) {
           return;
        }
    }
}
