package com.esiran.greenpay.admin.controller.agentpay;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.common.entity.APIException;

import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
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
            @RequestParam(required = false, defaultValue = "10") Integer size){
        return agentPayOrderService.selectPage(new Page<>(current,size),null);
    }
    /**
     * 代付订单补单
     */
    @GetMapping("/supplement/{orderNo}")
    public void supplement(@PathVariable String orderNo) throws APIException {
        AgentPayOrder agentPayOrder = agentPayOrderService.getOneByOrderNo(orderNo);
        if (agentPayOrder == null){
            throw new IllegalArgumentException("该订单不存在");
        }
        if (agentPayOrder.getStatus() != -1){
            throw new IllegalArgumentException("该订单不支持补单");
        }
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
    }

}
