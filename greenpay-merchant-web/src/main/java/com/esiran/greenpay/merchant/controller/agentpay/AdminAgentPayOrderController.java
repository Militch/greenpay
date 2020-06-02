package com.esiran.greenpay.merchant.controller.agentpay;

import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agentpay/order")
public class AdminAgentPayOrderController {
   private final IAgentPayOrderService agentPayOrderService;

    public AdminAgentPayOrderController(IAgentPayOrderService agentPayOrderService) {
        this.agentPayOrderService = agentPayOrderService;
    }

    @GetMapping("/list")
    public String list(){
        return "admin/agentpay/order/list";
    }

    @GetMapping("/list/{orderNo}/detail")
    public String detail(@PathVariable String orderNo, ModelMap modelMap){
        AgentPayOrderDTO order =  agentPayOrderService.getbyOrderNo(orderNo);
        modelMap.addAttribute("agentPayOrder", order);
        return "admin/agentpay/order/detail";
    }
}
