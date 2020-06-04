package com.esiran.greenpay.admin.controller.agentpay;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccountDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageDTO;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageAccountService;
import com.esiran.greenpay.agentpay.service.IAgentPayPassageService;
import com.esiran.greenpay.pay.entity.PassageAccountDTO;
import com.esiran.greenpay.pay.entity.PassageDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agentpay/passages")
public class APIAdminAgentPayPassageController {
    private final IAgentPayPassageService passageService;
    private final IAgentPayPassageAccountService passageAccountService;

    public APIAdminAgentPayPassageController(
            IAgentPayPassageService passageService,
            IAgentPayPassageAccountService passageAccountService) {
        this.passageService = passageService;
        this.passageAccountService = passageAccountService;
    }


    @GetMapping
    public IPage<AgentPayPassageDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        return passageService.selectPage(new Page<>(current,size),null);
    }
    @GetMapping("/{passageId}/accounts")
    public IPage<AgentPayPassageAccountDTO> listAcc(
            @PathVariable Integer passageId,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size){
        AgentPayPassageAccountDTO passageAccountDTO = new AgentPayPassageAccountDTO();
        passageAccountDTO.setPassageId(passageId);
        return passageAccountService.selectPage(new Page<>(current,size),passageAccountDTO);
    }
}
