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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class AgentPayBatchOrderTaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(AgentPayBatchOrderTaskRunner.class);
    @KafkaListener(topics = "grennpay_agentpay_batch_order_create")
    public void exec(ConsumerRecord<?, String> record) {
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());
        if (!kafkaMessage.isPresent()){
            return;
        }
        String msg = kafkaMessage.get();
        logger.info("消息来了----:{}",msg);
    }
}
