package com.esiran.greenpay.admin.controller.agentpay;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.common.entity.APIException;

import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agentpay/orders")
public class APIAdminAgentPayOrderController {

    private static final Gson g = new Gson();
    private final IAgentPayOrderService agentPayOrderService;
    private final RedisDelayQueueClient redisDelayQueueClient;
    public APIAdminAgentPayOrderController(IAgentPayOrderService agentPayOrderService, RedisDelayQueueClient redisDelayQueueClient) {
        this.agentPayOrderService = agentPayOrderService;
        this.redisDelayQueueClient = redisDelayQueueClient;
    }

    @GetMapping
    public IPage<AgentPayOrderDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size , AgentPayOrderDTO agentPayOrderDTO){
        return agentPayOrderService.selectPage(new Page<>(current,size),agentPayOrderDTO);
    }
}
