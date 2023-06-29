package com.esiran.greenpay.admin.controller.agentpay;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayOrderDTO;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.merchant.service.IPrepaidAccountService;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.service.IUserService;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/agentpay/order")
public class AdminAgentPayOrderController extends CURDBaseController {
    private static final Gson g = new Gson();
   private final IAgentPayOrderService agentPayOrderService;
    private final IPrepaidAccountService prepaidAccountService;
    private final IUserService userService;

    public AdminAgentPayOrderController(IAgentPayOrderService agentPayOrderService, IPrepaidAccountService prepaidAccountService, IUserService userService){
        this.agentPayOrderService = agentPayOrderService;
        this.prepaidAccountService = prepaidAccountService;
        this.userService = userService;
    }

    @GetMapping("/list")
    @PageViewHandleError
    public String list(HttpServletRequest request, ModelMap modelMap, OrderQueryDTO queryDTO){
        String qs = request.getQueryString();
        Map<String, String> qm = MapUtil.httpQueryString2map(qs);
        String qss = null;
        if (qm != null) {
            qss = MapUtil.map2httpQuery(qm);
        }
        modelMap.put("qs", qss);
        return "admin/agentpay/order/list";
    }

    @GetMapping("/list/{orderNo}/detail")
    public String detail(@PathVariable String orderNo, ModelMap modelMap){
        AgentPayOrderDTO order =  agentPayOrderService.getbyOrderNo(orderNo);
        modelMap.addAttribute("agentPayOrder", order);
        return "admin/agentpay/order/detail";
    }
    /**
     * 代付订单补单
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST, params = {"action=supplement"})
    public String supplement(@RequestParam String orderNo,@RequestParam String supplyPass) throws Exception {
        User user = theUser();
        try {
            boolean result = userService.verifyTOTPPass(user.getId(),supplyPass);
            if (!result) {
                throw new IllegalArgumentException("动态密码校验失败");
            }
        }catch (Exception e){
            throw new PostResourceException(e.getMessage());
        }
        AgentPayOrder agentPayOrder = agentPayOrderService.getOneByOrderNo(orderNo);
        if (agentPayOrder == null){
            throw new PostResourceException("该订单不存在");
        }
        if (agentPayOrder.getStatus() != -1){
            throw new PostResourceException("该订单不支持补单");
        }
        try {
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
                //redisDelayQueueClient.sendDelayMessage("supplement:query", queryMsg, 0);
            }
        } catch (APIException e) {
           throw new PostResourceException(e.getMessage());
        }
        return redirect("list");
    }
    /**
     * 代付退款
     * @param orderNo
     * @param supplyPass
     * @throws Exception
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST, params = {"action=refund"})
    public String refund(@RequestParam String orderNo
            ,@RequestParam String supplyPass) throws Exception {
        User user = theUser();
        try {
            boolean result = userService.verifyTOTPPass(user.getId(),supplyPass);
            if (!result) {
                throw new IllegalArgumentException("动态密码校验失败");
            }
        }catch (Exception e){
            throw new PostResourceException(e.getMessage());
        }
        AgentPayOrder agentPayOrder = agentPayOrderService.getOneByOrderNo(orderNo);
        if (agentPayOrder == null){
            throw new PostResourceException("订单不存在");
        }
        if (agentPayOrder.getStatus() != -1){
            throw new PostResourceException("该订单不支持退账");
        }
        LambdaUpdateWrapper<AgentPayOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AgentPayOrder::getStatus,-2)
                .set(AgentPayOrder::getUpdatedAt, LocalDateTime.now())
                .eq(AgentPayOrder::getId,agentPayOrder.getId());
        agentPayOrderService.update(updateWrapper);
        prepaidAccountService.updateBalance(agentPayOrder.getMchId()
                ,-(agentPayOrder.getAmount()+agentPayOrder.getFee())
                ,0);
        return redirect("list");
    }

    @GetMapping("/tagging/{orderNo}")
    public String tagging(@PathVariable String orderNo) {
        agentPayOrderService.tagging(orderNo);
        return "admin/agentpay/order/list";
    }

}
