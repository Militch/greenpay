package com.esiran.greenpay.merchant.controller.replace;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.agentpay.entity.AgentBatchInputVO;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayBatchInputDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderInputVO;
import com.esiran.greenpay.agentpay.service.IAgentPayBatchService;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.merchant.controller.CURDBaseController;
import com.esiran.greenpay.pay.entity.ReplacepayOrder;
import com.esiran.greenpay.pay.entity.ReplacepayRecharge;
import com.esiran.greenpay.pay.service.IReplacepayOrderService;
import com.esiran.greenpay.pay.service.IReplacepayRechargeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiMerchantReplaceController extends CURDBaseController {
    private final IReplacepayOrderService replacepayOrderService;
    private final IReplacepayRechargeService replacepayRechargeService;
    private final IAgentPayOrderService agentPayOrderService;
    private final IAgentPayBatchService agentPayBatchService;

    public ApiMerchantReplaceController(IReplacepayOrderService replacepayOrderService, IReplacepayRechargeService replacepayRechargeService, IAgentPayOrderService agentPayOrderService, IAgentPayBatchService agentPayBatchService) {
        this.replacepayOrderService = replacepayOrderService;
        this.replacepayRechargeService = replacepayRechargeService;
        this.agentPayOrderService = agentPayOrderService;
        this.agentPayBatchService = agentPayBatchService;
    }

    @GetMapping("/replaceLists")
    public IPage<ReplacepayOrder> replaceLists(Page<ReplacepayOrder> page){
        return replacepayOrderService.page(page);
    }

    @GetMapping("/recharge")
    public IPage<ReplacepayRecharge> recharges(Page<ReplacepayRecharge> page){
        return replacepayRechargeService.page(page);
    }

    @PostMapping("/replace/add")
    public Map replaceAdd(@Validated ReplacepayOrder replacepayOrder){
        Map m = new HashMap();
        replacepayOrder.setMchId("11111");
        replacepayOrder.setReplaceMoney(replacepayOrder.getReplaceMoney() * 100L);
        replacepayOrder.setCreatedAt(LocalDateTime.now());
        replacepayOrder.setReplaceId(IdWorker.getTimeId());
        replacepayOrder.setUpdatedAt(LocalDateTime.now());
        replacepayOrder.setStatus(0);
        try {
            replacepayOrderService.save(replacepayOrder);
        } catch (Exception e) {
            e.printStackTrace();
            m.put("code",0);
            m.put("msg","提交失败");
            return m;
        }
        m.put("code",1);
        m.put("msg","提交成功");
        return m;
    }
    @PostMapping("/replace/bath")
    public Map bath(@Validated AgentPayBatchInputDTO batchInputDTO){
        return null;
    }

    @PostMapping("/recharge/add")
    public Map rechargeAdd(@Validated ReplacepayRecharge replacepayRecharge){
        Map m = new HashMap();
        replacepayRecharge.setCreatedAt(LocalDateTime.now());
        replacepayRecharge.setUpdatedAt(LocalDateTime.now());
        replacepayRecharge.setStatus(0);
        replacepayRecharge.setRechargeId(IdWorker.getTimeId());
        replacepayRecharge.setMchId("1");
        replacepayRecharge.setRechargeMoney(replacepayRecharge.getRechargeMoney() * 100L);
        try {
            replacepayRechargeService.save(replacepayRecharge);
        } catch (Exception e) {
            e.printStackTrace();
            m.put("code",0);
            m.put("msg","提交失败");
            return m;
        }
        m.put("code",1);
        m.put("msg","提交成功");
        return m;
    }


    //    @GetMapping("/agentpay/orders")
//    public IPage<AgentPayOrderDTO> list(
//            @RequestParam(required = false,defaultValue = "1") Integer current,
//            @RequestParam(required = false, defaultValue = "10") Integer size){
//        return agentPayOrderService.selectPage(new Page<>(current,size),null);
//    }

    @GetMapping("/replace/orders")
    public List<AgentPayOrderDTO> list(@RequestParam(required = false,defaultValue = "1") Integer current,
                                       @RequestParam(required = false, defaultValue = "10") Integer size, AgentPayOrderInputVO agentPayOrderInputVO){
        Integer mchId = theUser().getId();
        agentPayOrderInputVO.setMchId(mchId);
        return agentPayOrderService.agentPayOrderList(new Page<>(current,size),  agentPayOrderInputVO);
    }


    @GetMapping("/agentpay/batch")
    public List<AgentPayBatchDTO> list(
            @RequestParam(required = false,defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size, AgentBatchInputVO agentBatchInputVO){
        Integer mchId = theUser().getId();
        agentBatchInputVO.setMchId(mchId);
        return agentPayBatchService.selectPage(new Page<>(current,size),agentBatchInputVO);
    }
}
