package com.esiran.greenpay.merchant.controller.batch;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderInputVO;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agentpay")
public class AgentPayOrderController {

    private final IAgentPayOrderService agentPayOrderService;

    public AgentPayOrderController(IAgentPayOrderService agentPayOrderService) {
        this.agentPayOrderService = agentPayOrderService;
    }

//    @GetMapping("/orders")
//    public IPage<AgentPayOrderDTO> list(
//            @RequestParam(required = false,defaultValue = "1") Integer current,
//            @RequestParam(required = false, defaultValue = "10") Integer size){
//        return agentPayOrderService.selectPage(new Page<>(current,size),null);
//    }
    @GetMapping("/orders")
    public List<AgentPayOrderDTO> list(@RequestParam(required = false,defaultValue = "1") Integer current,
                                       @RequestParam(required = false, defaultValue = "10") Integer size, AgentPayOrderInputVO agentPayOrderInputVO){

        return agentPayOrderService.agentPayOrderList(new Page<>(current,size),  agentPayOrderInputVO);
    }

}
