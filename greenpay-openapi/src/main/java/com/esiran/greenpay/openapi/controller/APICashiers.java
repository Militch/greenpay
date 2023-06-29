package com.esiran.greenpay.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.*;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.common.util.ResUtil;
import com.esiran.greenpay.common.util.UrlSafeB64;
import com.esiran.greenpay.merchant.entity.ApiConfig;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.openapi.entity.CashierInputDTO;
import com.esiran.greenpay.openapi.entity.Invoice;
import com.esiran.greenpay.openapi.entity.QueryDTO;
import com.esiran.greenpay.openapi.security.OpenAPISecurityUtils;
import com.esiran.greenpay.openapi.service.ICashierService;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.plugin.PayOrderFlow;
import com.esiran.greenpay.pay.service.IInterfaceService;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.pay.service.IOrderService;
import com.esiran.greenpay.pay.service.IProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/v1/cashiers")
public class APICashiers {
    private static final Logger logger = LoggerFactory.getLogger(APICashiers.class);
    private final ICashierService cashierService;
    private final IApiConfigService apiConfigService;
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    private final IProductService productService;
    private final IInterfaceService interfaceService;
    private final PluginLoader pluginLoader;
    private static final Gson gson = new GsonBuilder().create();
    @Value("${greenpay.web.hostname:http://localhost}")
    private String webHostname;
    private static final ModelMapper modelMapper = new ModelMapper();
    public APICashiers(
            ICashierService cashierService,
            IApiConfigService apiConfigService,
            IOrderService orderService,
            IOrderDetailService orderDetailService,
            IProductService productService, IInterfaceService interfaceService,
            PluginLoader pluginLoader
    ) {
        this.cashierService = cashierService;
        this.apiConfigService = apiConfigService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.productService = productService;
        this.interfaceService = interfaceService;
        this.pluginLoader = pluginLoader;
    }
    @RequestMapping("/qr/orders")
    public String createQrPage(@Valid CashierInputDTO inputDTO) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        String orderNo = payOrder.getOrder().getOrderNo();
        Interface ins = interfaceService.getById(payOrder.getOrderDetail().getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        if (scenarios == 1){
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            try {
                Plugin<PayOrder> payOrderPlugin =
                        pluginLoader.loadForClassPath(ins.getInterfaceImpl());
                payOrderPlugin.apply(payOrderFlow);
                payOrderFlow.execDependent("create");
                Map<String,Object> results = payOrderFlow.getResults();
                orderDetailService.updatePayCredentialByOrderNo(orderNo,results);
            } catch (Exception e) {
                throw new APIException("请求支付通道失败","REQUEST_ERROR");
            }
        } else if (!(scenarios == 5 || scenarios ==6)){
            throw new APIException("支付场景暂不支持该渠道支付","REQUEST_ERROR");
        }
        return String.format("redirect:/v1/cashiers/qr/orders/%s",orderNo);
    }
    @GetMapping("/qr/orders/{orderNo}")
    public String qrOrder(
            HttpServletResponse response,
            @PathVariable String orderNo,
            ModelMap modelMap) throws Exception {
        Order order = orderService.getOneByOrderNo(orderNo);
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (order == null || orderDetail == null) {
            response.setStatus(404);
            return null;
        }
        Interface ins = interfaceService.getById(orderDetail.getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        String qrCodeUrl = null;
        String style = scenarios == 5?"wechat":scenarios == 6?"alipay":null;
        if (scenarios == 1){
            String payCredential = orderDetail.getPayCredential();
            Map<String,Object> credentialMap = gson.fromJson(payCredential,
                    new TypeToken<Map<String,Object>>(){}.getType());
            qrCodeUrl = (String) credentialMap.get("codeUrl");
            String prodCode = order.getPayProductCode();
            style = prodCode.startsWith("wx_") ? "wechat":
                    prodCode.startsWith("ali_") ? "alipay": null;
        }else if(scenarios == 5){
            String redirectUrl = String.format(
                    "%s/v1/cashiers/wx_pub/orders/%s",
                    webHostname, order.getOrderNo());
            qrCodeUrl = String.format(
                    "%s/v1/helper/wx/openid?redirectUrl=%s",
                    webHostname,URLEncoder.encode(redirectUrl,"UTF-8"));
        }else if(scenarios == 6){
            qrCodeUrl = "";
        }
        if (StringUtils.isEmpty(qrCodeUrl)){
            response.setStatus(500);
            return null;
        }
        String qrCodeImgUrl = String.format(
                "/v1/helper/qr/builder?codeUrl=%s&style=w260h260",
                URLEncoder.encode(qrCodeUrl, "UTF-8"));
        modelMap.addAttribute("style",style);
        OrderDTO orderDTO = orderService.getByOrderNo(orderNo);
        modelMap.addAttribute("order",orderDTO);
        modelMap.addAttribute("qrCodeImgUrl",qrCodeImgUrl);
        return "cashier/qr_pc";
    }

    @GetMapping("/wx_pub/orders/{orderNo}")
    public String wxPubOrders(
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap modelMap,
            @RequestParam String openId,
            @PathVariable String orderNo){
        Order order = orderService.getOneByOrderNo(orderNo);
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (order == null || orderDetail == null || order.getStatus() != 1 ){
            response.setStatus(404);
            return null;
        }
        Interface ins = interfaceService.getById(orderDetail.getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        if (scenarios != 5){
            response.setStatus(404);
            return null;
        }
        Map<String,Object> upstreamExtra = new LinkedHashMap<>();
        upstreamExtra.put("openId",openId);
        String upstreamExtraJson = gson.toJson(upstreamExtra);
        orderDetail.setUpstreamExtra(upstreamExtraJson);
        orderDetailService.updateById(orderDetail);
        String notifyReceiveUrl = String.format(
                "%s/v1/invoices/%s/callback",
                webHostname,order.getOrderNo());
        PayOrder payOrder = new PayOrder();
        payOrder.setOrder(order);
        payOrder.setOrderDetail(orderDetail);
        payOrder.setNotifyReceiveUrl(notifyReceiveUrl);
        Map<String,Object> results;
        try {
            Plugin<PayOrder> payOrderPlugin = pluginLoader.loadForClassPath(ins.getInterfaceImpl());
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            payOrderPlugin.apply(payOrderFlow);
            payOrderFlow.execDependent("create");
            results = payOrderFlow.getResults();
            orderDetailService.updatePayCredentialByOrderNo(orderNo,results);
        } catch (Exception e) {
            response.setStatus(404);
            ResUtil.printAlert(response,"下单失败");
            return null;
        }
        String wxMpAppId = "wx2aeda339f56138bf";
        String wxMpSecret = "731787f51247a33c4ff210cd613dd780";
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(wxMpAppId);
        config.setSecret(wxMpSecret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        String currentUrl = request.getRequestURL().append("?")
                .append(request.getQueryString()).toString();
        WxJsapiSignature wxConfig;
        try {
            wxConfig = wxMpService.createJsapiSignature(currentUrl);
        } catch (WxErrorException e) {
            e.printStackTrace();
            WxError error = e.getError();
            if (error == null){
                ResUtil.printAlert(response,"未知错误，请联系管理员");
                return null;
            }
            response.setStatus(403);
            int code = error.getErrorCode();
            if (code == 40164){
                ResUtil.printAlert(response,"请配置微信公众号IP白名单");
            }
            return null;
        }
        modelMap.put("order", order);
        modelMap.put("orderAmountDisplay", NumberUtil.amountFen2Yuan(order.getAmount()));
        modelMap.put("payAttr",results);
        modelMap.put("wxConfig",wxConfig);
        return "cashier/wx_jsapi";
    }

    @RequestMapping("/pc/orders")
    public String createPcOrder(@Valid CashierInputDTO inputDTO, HttpServletRequest request) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        String orderNo = payOrder.getOrder().getOrderNo();
        Interface ins = interfaceService.getById(payOrder.getOrderDetail().getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        if (scenarios == 2){
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            try {
                Plugin<PayOrder> payOrderPlugin =
                        pluginLoader.loadForClassPath(ins.getInterfaceImpl());
                payOrderPlugin.apply(payOrderFlow);
                payOrderFlow.execDependent("create");
                Map<String,Object> results = payOrderFlow.getResults();
                orderDetailService.updatePayCredentialByOrderNo(orderNo,results);
            } catch (Exception e) {
                throw new APIException("请求支付通道失败","REQUEST_ERROR");
            }
        }else {
            throw new APIException("支付场景暂不支持该渠道支付","REQUEST_ERROR");
        }
        return String.format("redirect:/v1/cashiers/pc/orders/%s",orderNo);
    }
    @GetMapping("/pc/orders/{orderNo}")
    public String pcOrder(
            HttpServletResponse response,
            @PathVariable String orderNo) throws Exception {
        Order order = orderService.getOneByOrderNo(orderNo);
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (order == null || orderDetail == null) {
            response.setStatus(404);
            return null;
        }
        Interface ins = interfaceService.getById(orderDetail.getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        if (scenarios != 2){
            response.setStatus(404);
            return null;
        }
        String payCredential = orderDetail.getPayCredential();
        Map<String, Object> map = MapUtil.jsonString2objMap(payCredential);
        assert map != null;
        String form = (String) map.get("form");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println(form);
        return null;
    }

    @RequestMapping("/h5/orders")
    public void createH5Order(
            @Valid CashierInputDTO inputDTO,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        String orderNo = payOrder.getOrder().getOrderNo();
        Interface ins = interfaceService.getById(payOrder.getOrderDetail().getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        String html = null;
        if (scenarios == 3){
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            try {
                Plugin<PayOrder> payOrderPlugin =
                        pluginLoader.loadForClassPath(ins.getInterfaceImpl());
                payOrderPlugin.apply(payOrderFlow);
                payOrderFlow.execDependent("create");
                Map<String,Object> results = payOrderFlow.getResults();
                html = (String) results.get("html");
                orderDetailService.updatePayCredentialByOrderNo(orderNo,results);
            } catch (Exception e) {
                throw new APIException("请求支付通道失败","REQUEST_ERROR");
            }
        } else {
            throw new APIException("支付场景暂不支持该渠道支付","REQUEST_ERROR");
        }
        response.setCharacterEncoding("UTF-8");
        PrintWriter pw = response.getWriter();
        pw.println(html);
        pw.flush();
        pw.close();
    }

    @RequestMapping("/wx_pub/orders")
    public String createWxPubOrder(@Valid CashierInputDTO inputDTO, HttpServletRequest request) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        String orderNo = payOrder.getOrder().getOrderNo();
        Interface ins = interfaceService.getById(payOrder.getOrderDetail().getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        if (scenarios == 5){
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            try {
                Plugin<PayOrder> payOrderPlugin =
                        pluginLoader.loadForClassPath(ins.getInterfaceImpl());
                payOrderPlugin.apply(payOrderFlow);
                payOrderFlow.execDependent("create");
                Map<String,Object> results = payOrderFlow.getResults();
                orderDetailService.updatePayCredentialByOrderNo(orderNo,results);
            } catch (Exception e) {
                throw new APIException("请求支付通道失败","REQUEST_ERROR");
            }
        }else {
            throw new APIException("支付场景暂不支持该渠道支付","REQUEST_ERROR");
        }
        return null;
    }


    @RequestMapping("/ali_pub/orders")
    public String createAliPubOrder(@Valid CashierInputDTO inputDTO, HttpServletRequest request) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        String orderNo = payOrder.getOrder().getOrderNo();
        Interface ins = interfaceService.getById(payOrder.getOrderDetail().getPayInterfaceId());
        Integer scenarios = ins.getScenarios();
        if (scenarios == 6){
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            try {
                Plugin<PayOrder> payOrderPlugin =
                        pluginLoader.loadForClassPath(ins.getInterfaceImpl());
                payOrderPlugin.apply(payOrderFlow);
                payOrderFlow.execDependent("create");
                Map<String,Object> results = payOrderFlow.getResults();
                orderDetailService.updatePayCredentialByOrderNo(orderNo,results);
            } catch (Exception e) {
                throw new APIException("请求支付通道失败","REQUEST_ERROR");
            }
        }else {
            throw new APIException("支付场景暂不支持该渠道支付","REQUEST_ERROR");
        }
        return null;
    }



    @PostMapping("/pages")
    @ResponseBody
    public Invoice cashiersPagesPost(
            @RequestParam String orderNo,
            @RequestParam String channelExtra) throws APIException {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Order order = orderService.getOneByOrderNo(orderNo);
        if (order == null) throw new APIException("订单不存在","RESOURCE_NOT_FOUND");
        if (order.getStatus() > 1) throw new APIException("订单已完成支付","ORDER_STATUS_PAID");
        if (order.getStatus() < 1 ) throw new APIException("订单已过期或异常","ORDER_STATUS_ERROR");
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (orderDetail == null ) throw new APIException("系统异常[订单详情不存在]","SYSTEM_ERROR");
        String productCode = order.getPayProductCode();
        if (productCode == null || productCode.length() == 0)
            throw new APIException("系统异常[订单支付渠道不存在]","SYSTEM_ERROR");
        orderDetail.setUpstreamExtra(channelExtra);
        orderDetailService.updateById(orderDetail);
        String notifyReceiveUrl = String.format(
                "%s/v1/invoices/%s/callback",
                webHostname,order.getOrderNo());
        PayOrder payOrder = new PayOrder();
        payOrder.setOrder(order);
        payOrder.setOrderDetail(orderDetail);
        payOrder.setNotifyReceiveUrl(notifyReceiveUrl);
        Interface interfaces = interfaceService.getById(orderDetail.getPayInterfaceId());
        Map<String,Object> results;
        try {
            Plugin<PayOrder> payOrderPlugin = pluginLoader.loadForClassPath(interfaces.getInterfaceImpl());
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            payOrderPlugin.apply(payOrderFlow);
            payOrderFlow.execDependent("create");
            results = payOrderFlow.getResults();
            orderDetailService.updatePayCredentialByOrderNo(order.getOrderNo(),results);
        } catch (Exception e) {
            if (e instanceof APIException){
                String code = ((APIException) e).getCode();
                String message = e.getMessage();
                throw new APIException(message,code);
            }
            throw new APIException(String.format("系统异常[%s]",e.getMessage()),"SYSTEM_ERROR");
        }
        Invoice out = modelMapper.map(order,Invoice.class);
        out.setChannel(order.getPayProductCode());
        out.setCredential(results);
        return out;
    }

    @GetMapping("/pages/wx/order")
    public String payOrder(
            @RequestParam String orderNo,
            @RequestParam String openId,
            ModelMap modelMap,
            HttpServletRequest request,
            HttpServletResponse response){
        logger.info("orderNo: {}, openId: {}",orderNo,openId);
        Order order = orderService.getOneByOrderNo(orderNo);
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (order == null || orderDetail == null || order.getStatus() != 1 ){
            response.setStatus(404);
            return null;
        }
        Map<String,Object> upstreamExtra = new LinkedHashMap<>();
        upstreamExtra.put("openId",openId);
        String upstreamExtraJson = gson.toJson(upstreamExtra);
        orderDetail.setUpstreamExtra(upstreamExtraJson);
        orderDetailService.updateById(orderDetail);
        String notifyReceiveUrl = String.format(
                "%s/v1/invoices/%s/callback",
                webHostname,order.getOrderNo());
        PayOrder payOrder = new PayOrder();
        payOrder.setOrder(order);
        payOrder.setOrderDetail(orderDetail);
        payOrder.setNotifyReceiveUrl(notifyReceiveUrl);
        Interface interfaces = interfaceService.getById(orderDetail.getPayInterfaceId());
        Map<String,Object> results;
        try {
            Plugin<PayOrder> payOrderPlugin = pluginLoader.loadForClassPath(interfaces.getInterfaceImpl());
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            payOrderPlugin.apply(payOrderFlow);
            payOrderFlow.execDependent("create");
            results = payOrderFlow.getResults();
        } catch (Exception e) {
            response.setStatus(404);
            return null;
        }
        String wxMpAppId = "wx2aeda339f56138bf";
        String wxMpSecret = "731787f51247a33c4ff210cd613dd780";
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(wxMpAppId);
        config.setSecret(wxMpSecret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        String currentUrl = request.getRequestURL().append("?")
                .append(request.getQueryString()).toString();
        WxJsapiSignature wxConfig;
        try {
            wxConfig = wxMpService.createJsapiSignature(currentUrl);
        } catch (WxErrorException e) {
            e.printStackTrace();
            response.setStatus(404);
            return null;
        }
        modelMap.put("order",order);
        modelMap.put("orderAmountDisplay", NumberUtil.amountFen2Yuan(order.getAmount()));
        modelMap.put("payAttr",results);
        modelMap.put("wxConfig",wxConfig);
        return "cashier/wx";
    }
    @GetMapping("/flow")
    @ResponseBody
    public String queryOrderStatus(@RequestParam String orderNo) throws APIException {
        String redirectUrl = "";
        QueryDTO dto = new QueryDTO();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderService.getOne(wrapper);
        if (order == null || !(order.getStatus() == 2 || order.getStatus() == 3)) {
            throw new APIException("订单不存在或状态异常", "ORDER_NOT_FOUND", 404);
        }
        if (!StringUtils.isEmpty(order.getRedirectUrl())) {
            ApiConfig apiConfig = apiConfigService.getOneByMerchantId(order.getMchId());
            if (apiConfig == null) {
                throw new APIException("系统错误", "SYSTEM_ERROR", 500);
            }
            Map<String, String> params = new HashMap<>();
            params.put("orderNo", order.getOrderNo());
            params.put("outOrderNo", order.getOutOrderNo());
            params.put("channel", order.getPayProductCode());
            params.put("subject", order.getSubject());
            if (order.getBody() != null) {
                params.put("body", order.getBody());
            }
            params.put("amount", String.valueOf(order.getAmount()));
            params.put("fee", String.valueOf(order.getFee()));
            params.put("appId", order.getAppId());
            params.put("status", String.valueOf(order.getStatus()));
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            params.put("signType", "rsa");
            String principal = MapUtil.sortAndSerialize(params);
            SignType signType = new RSA2SignType(principal);
            String sign = signType.sign2(apiConfig.getPrivateKey());
            sign = UrlSafeB64.encode(sign);
            return String.format("%s?%s&sign=%s",order.getRedirectUrl(),principal,sign);

        }
        return redirectUrl;
    }

    @GetMapping("/query/orderStatus")
    @ResponseBody
    public Map<String,Object> queryStatus(String orderNo) throws APIException {
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderDetail(orderDetail);
        PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
        Interface ins = interfaceService.getById(orderDetail.getPayInterfaceId());
        try {
            Plugin<PayOrder> payOrderPlugin =
                    pluginLoader.loadForClassPath(ins.getInterfaceImpl());
            payOrderPlugin.apply(payOrderFlow);
            payOrderFlow.execDependent("query");
            Map<String, Object> results = payOrderFlow.getResults();
            results.put("url",String.format("%s/v1/cashiers/success/%s",webHostname,orderNo));
            return results;
        }catch (Exception e) {
            throw new APIException("查询订单状态失败","REQUEST_ERROR");
        }
    }
    @GetMapping("/success/{orderNo}")
    public String success(@PathVariable String orderNo
            , ModelMap modelMap){
        OrderDTO order = orderService.getByOrderNo(orderNo);
        modelMap.addAttribute("order",order);
        return "cashier/success";
    }
}
