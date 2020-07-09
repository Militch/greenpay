package com.esiran.greenpay.admin.controller.order;

import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.merchant.entity.ApiConfigDTO;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.pay.entity.OrderDTO;
import com.esiran.greenpay.pay.entity.OrderDetailDTO;
import com.esiran.greenpay.pay.entity.OrderQueryDTO;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.pay.service.IOrderService;
import com.esiran.greenpay.pay.service.impl.OrderNotifyService;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/order")
public class AdminPayOrderController extends CURDBaseController {
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    private final IUserService userService;
    private final OrderNotifyService orderNotifyService;
    private final IApiConfigService iApiConfigService;
    private static final Logger logger = LoggerFactory.getLogger(AdminPayOrderController.class);
    public AdminPayOrderController(IOrderService orderService, IOrderDetailService orderDetailService, IUserService userService, OrderNotifyService orderNotifyService, IApiConfigService iApiConfigService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.userService = userService;
        this.orderNotifyService = orderNotifyService;
        this.iApiConfigService = iApiConfigService;
    }


    @GetMapping("/list")
    @PageViewHandleError
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
        return "admin/order/list";
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST, params = {"action=notify"})
    public String notify(@RequestParam String orderNo, @RequestParam Integer mchId) throws PostResourceException {
        if (mchId == null) {
            throw new PostResourceException("商户ID不正确");
        }
        OrderDTO order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            throw new PostResourceException("订单不存在");
        }
        //查询商户授权码
        ApiConfigDTO merchant = iApiConfigService.findByMerchantId(mchId);
        if (merchant == null) {
            throw new PostResourceException("未查詢到商戶");
        }
        String signType = "md5";
        String credential = merchant.getApiSecurity();
        String apiPrivKey = merchant.getPrivateKey();
        if (apiPrivKey != null && apiPrivKey.length() > 0){
            signType = "rsa";
            credential = apiPrivKey;
        }
        //发送通知
        boolean b = orderNotifyService.notifyByOrderNo(orderNo, credential,signType);
        if (!b) {
            throw new PostResourceException("通知成功，商户返回值校验失败");
        }
        return redirect("/admin/order/list");
    }


    @RequestMapping(value = "/list",method = RequestMethod.POST, params = {"action=supply"})
    public String supply(@RequestParam String orderNo, @RequestParam String supplyPass) throws PostResourceException {
        User user = theUser();
        try {
            boolean result = userService.verifyTOTPPass(user.getId(),supplyPass);
            if (!result)
                throw new IllegalArgumentException("动态密码校验失败");
        }catch (Exception e){
            throw new PostResourceException(e.getMessage());
        }
        logger.info("处理补单逻辑");
        return redirect("/admin/order/list");
    }


    @GetMapping("/list/{orderNo}/detail")
    public String detail(@PathVariable String orderNo, ModelMap modelMap){
        OrderDTO order = orderService.getByOrderNo(orderNo);
        OrderDetailDTO orderDetail = orderDetailService.getByOrderNo(orderNo);
        modelMap.addAttribute("order", order);
        modelMap.addAttribute("orderDetail", orderDetail);
        return "admin/order/detail";
    }


}
