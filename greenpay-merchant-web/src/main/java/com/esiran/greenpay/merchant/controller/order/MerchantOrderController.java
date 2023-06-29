package com.esiran.greenpay.merchant.controller.order;

import com.esiran.greenpay.merchant.controller.CURDBaseController;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.pay.service.IOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class MerchantOrderController extends CURDBaseController {

    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;

    public MerchantOrderController(IOrderService orderService, IOrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/list")
    public String orderList(){
        return "merchant/order/orderlist";
    }
    @GetMapping("/extract")
    public String extracts(){
        return "merchant/order/extractlist";
    }
    @GetMapping("/payextract")
    public String payextract(Model model){
        Merchant merchant = theUser();
        model.addAttribute("merId",merchant.getId());
        return "merchant/order/payextract";
    }
    @GetMapping("/list/{orderNo}/detail")
    public String detail(@PathVariable String orderNo, ModelMap modelMap){
        OrderDTO order = orderService.getByOrderNo(orderNo);
        OrderDetailDTO orderDetail = orderDetailService.getByOrderNo(orderNo);
        modelMap.addAttribute("order", order);
        modelMap.addAttribute("orderDetail", orderDetail);
        return "merchant/order/detail";
    }



}
