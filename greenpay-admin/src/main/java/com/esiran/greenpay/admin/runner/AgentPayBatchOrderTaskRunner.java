package com.esiran.greenpay.admin.runner;

import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AgentPayBatchOrderTaskRunner {
    private static final Gson g = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(AgentPayBatchOrderTaskRunner.class);
    private final IAgentPayOrderService agentPayOrderService;

    public AgentPayBatchOrderTaskRunner(IAgentPayOrderService agentPayOrderService) {
        this.agentPayOrderService = agentPayOrderService;
    }

    @KafkaListener(topics = "grennpay_agentpay_batch_order_create")
    public void exec(ConsumerRecord<?, String> record) {
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());
        if (!kafkaMessage.isPresent()){
            return;
        }
        String msg = kafkaMessage.get();
        logger.info("消息来了----:{}",msg);
        AgentPayOrder agentPayOrder = g.fromJson(msg, new TypeToken<AgentPayOrder>() {
        }.getType());
        agentPayOrderService.createOneBatchOrder(agentPayOrder);
    }
}
