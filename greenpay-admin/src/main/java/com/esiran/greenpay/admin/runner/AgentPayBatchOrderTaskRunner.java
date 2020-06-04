package com.esiran.greenpay.admin.runner;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AgentPayBatchOrderTaskRunner {
    private static final Gson g = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(AgentPayBatchOrderTaskRunner.class);
    private final IAgentPayOrderService agentPayOrderService;
    private final IPrepaidAccountService prepaidAccountService;
    private final RedisDelayQueueClient redisDelayQueueClient;

    public AgentPayBatchOrderTaskRunner(IAgentPayOrderService agentPayOrderService, IPrepaidAccountService prepaidAccountService, RedisDelayQueueClient redisDelayQueueClient) {
        this.agentPayOrderService = agentPayOrderService;
        this.prepaidAccountService = prepaidAccountService;
        this.redisDelayQueueClient = redisDelayQueueClient;
    }

    @KafkaListener(topics = "grennpay_agentpay_batch_order_create")
    public void exec(ConsumerRecord<?, String> record) throws APIException {
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());
        if (!kafkaMessage.isPresent()){
            return;
        }
        String msg = kafkaMessage.get();
        logger.info("消息来了----:{}",msg);
        AgentPayOrder agentPayOrder = g.fromJson(msg, new TypeToken<AgentPayOrder>() {
        }.getType());
        String status = agentPayOrderService.createOneBatchOrder(agentPayOrder);
        LambdaUpdateWrapper<AgentPayOrder> updateWrapperwrapper = new LambdaUpdateWrapper<>();
        if (status.equals("30")){
            updateWrapperwrapper.set(AgentPayOrder::getStatus,-1)
                    .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                    .eq(AgentPayOrder::getId,agentPayOrder.getId());
            agentPayOrderService.update(updateWrapperwrapper);
        }
        if (status.equals("20")){
            updateWrapperwrapper.set(AgentPayOrder::getStatus,3)
                    .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                    .eq(AgentPayOrder::getId,agentPayOrder.getId());
            agentPayOrderService.update(updateWrapperwrapper);
        }
        if (status.equals("40")) {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("orderNo", agentPayOrder.getOrderNo());
            queryMap.put("count", "1");
            String queryMsg = g.toJson(queryMap);
            redisDelayQueueClient.sendDelayMessage("agentpay:query", queryMsg, 0);
        }
        prepaidAccountService.updateBalance(agentPayOrder.getMchId()
                ,0
                ,(agentPayOrder.getAmount()+agentPayOrder.getFee()));
    }
}
