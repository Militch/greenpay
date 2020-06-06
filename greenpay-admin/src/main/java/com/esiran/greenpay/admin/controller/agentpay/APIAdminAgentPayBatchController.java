package com.esiran.greenpay.admin.controller.agentpay;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentBatchInputVO;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchDTO;
import com.esiran.greenpay.agentpay.service.IAgentPayBatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agentpay/batch")
public class APIAdminAgentPayBatchController {

    private final IAgentPayBatchService agentPayBatchService;

    public APIAdminAgentPayBatchController(IAgentPayBatchService agentPayBatchService) {
        this.agentPayBatchService = agentPayBatchService;
    }


    @GetMapping
    public List<AgentPayBatchDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        return agentPayBatchService.selectPage(new Page<>(current,size),null);
    }
}
