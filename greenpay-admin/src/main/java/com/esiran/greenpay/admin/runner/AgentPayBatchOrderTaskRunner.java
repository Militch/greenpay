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
public class AgentPayBatchOrderTaskRunner implements DelayQueueTaskRunner {
    private final Gson g = new Gson();
    private final IAgentPayOrderService agentPayOrderService;
    private final IPrepaidAccountService prepaidAccountService;
    private final RedisDelayQueueClient redisDelayQueueClient;
    public AgentPayBatchOrderTaskRunner(IAgentPayOrderService agentPayOrderService, IPrepaidAccountService prepaidAccountService, RedisDelayQueueClient redisDelayQueueClient) {
        this.agentPayOrderService = agentPayOrderService;
        this.prepaidAccountService = prepaidAccountService;
        this.redisDelayQueueClient = redisDelayQueueClient;
    }

    @Override
    public void exec(String content) {
//        agentPayOrderService
    }
}
