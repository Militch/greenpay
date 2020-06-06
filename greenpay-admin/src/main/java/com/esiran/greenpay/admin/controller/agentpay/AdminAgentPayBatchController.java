package com.esiran.greenpay.admin.controller.agentpay;

import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.pay.service.IOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/agentpay/batch")
public class AdminAgentPayBatchController {
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;

    public AdminAgentPayBatchController(
            IOrderService orderService,
            IOrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/list")
    public String list(HttpServletRequest request,
                       ModelMap modelMap,
                       OrderQueryDTO orderQueryDTO) {
        String qs = request.getQueryString();
        Map<String,String> qm = MapUtil.httpQueryString2map(qs);
        String qss = null;
        if (qm != null){
            qss = MapUtil.map2httpQuery(qm);
        }
        modelMap.put("qs",qss);
        return "admin/agentpay/batch/list";
    }

    @GetMapping("/list/{orderNo}/detail")
    public String detail(@PathVariable String orderNo, ModelMap modelMap){
        OrderDTO order = orderService.getByOrderNo(orderNo);
        OrderDetailDTO orderDetail = orderDetailService.getByOrderNo(orderNo);
        modelMap.addAttribute("order", order);
        modelMap.addAttribute("orderDetail", orderDetail);
        return "admin/agentpay/order/detail";
    }
}
