package com.esiran.greenpay.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.*;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.common.util.SignReqUtil;
import com.esiran.greenpay.common.util.UrlSafeB64;
import com.esiran.greenpay.merchant.entity.ApiConfig;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
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
import io.swagger.models.auth.In;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/v1/cashiers")
public class APICashiers {
    private static final Logger logger = LoggerFactory.getLogger(APICashiers.class);
    private final ICashierService cashierService;
    private final IApiConfigService apiConfigService;
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    private final IInterfaceService interfaceService;
    private final PluginLoader pluginLoader;
    private final RedisDelayQueueClient redisDelayQueueClient;
    private static final Gson gson = new GsonBuilder().create();
    @Value("${greenpay.web.hostname:http://localhost}")
    private String webHostname;
    private static final ModelMapper modelMapper = new ModelMapper();
    public APICashiers(
            ICashierService cashierService,
            IApiConfigService apiConfigService,
            IOrderService orderService,
            IOrderDetailService orderDetailService,
            IInterfaceService interfaceService,
            PluginLoader pluginLoader,
            RedisDelayQueueClient redisDelayQueueClient) {
        this.cashierService = cashierService;
        this.apiConfigService = apiConfigService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.interfaceService = interfaceService;
        this.pluginLoader = pluginLoader;
        this.redisDelayQueueClient = redisDelayQueueClient;
    }
    @RequestMapping("/qr/pages")
    public String createQrPage(@Valid CashierInputDTO inputDTO) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        String orderNo = payOrder.getOrder().getOrderNo();
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
        String qrCodeUrl = "http://www.baidu.com";
        if (ins.getScenarios() == 4){
            String redirectUrl = String.format("%s/v1/cashiers/pay/wx/order?orderNo=%s",
                    webHostname, orderNo);
            qrCodeUrl =  String.format("redirect:/v1/helper/wx/openid");
        }else if(ins.getScenarios() == 5){

        }
        String qrCodeImgUrl = String.format(
                "/v1/helper/qr/builder?codeUrl=%s&style=w300h300",
                URLEncoder.encode(qrCodeUrl, "UTF-8"));
        modelMap.addAttribute("qrCodeImgUrl",qrCodeImgUrl);
//        return String.format("cashier/%s","qr_pc");
        return "cashier/qr_pc";
    }
    @GetMapping("/qr/proxy/{orderNo}")
    public String qrProxy(@PathVariable String orderNo){
        return "";
    }
    @RequestMapping
    public String create(@Valid CashierInputDTO inputDTO, HttpServletRequest request) throws Exception {
        Merchant merchant = OpenAPISecurityUtils.getSubject();
        String productCode = inputDTO.getChannel();
        PayOrder payOrder = cashierService.createCashierByInput(productCode, inputDTO, merchant);
        ApiConfig apiConfig = apiConfigService.getOneByMerchantId(merchant.getId());
        String timestamp = String.valueOf(System.currentTimeMillis());
        Map<String,String> reqMap = new HashMap<>();
        reqMap.put("timestamp", timestamp);
        reqMap.put("signType", "rsa");
        reqMap.put("apiKey", apiConfig.getApiKey());
        if (productCode.equals("wx_jsapi")){
            String redirectUrl = String.format("%s/v1/cashiers/pay/wx/order?orderNo=%s",
                    webHostname, payOrder.getOrder().getOrderNo());
            reqMap.put("redirectUrl",redirectUrl);
            String req = MapUtil.sortAndSerialize(reqMap);
            SignType signType = new Md5SignType(req);
            SignVerify verify = signType.sign(apiConfig.getApiSecurity());
            String sign = verify.getSign();
            return String.format("redirect:/v1/helper/wx/openid?%s&sign=%s",req,sign);
        }else if(productCode.equals("wx_scan")){
            reqMap.put("orderNo",payOrder.getOrder().getOrderNo());
            String req = MapUtil.sortAndSerialize(reqMap);
            SignType signType = new RSA2SignType(req);
            String sign = signType.sign2(apiConfig.getPrivateKey());
            sign = UrlSafeB64.encode(sign);
            return String.format("redirect:/v1/cashiers/pages?%s&sign=%s",req,sign);
        }
        return null;
    }

    @GetMapping("/pages")
    public String cashiersPages(
            @RequestParam String orderNo,
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap modelMap){
        try {
            SignType signType = SignReqUtil.verifySignParam(request,1200000);
            String apiKey = request.getParameter("apiKey");
            String sign = request.getParameter("sign");
            LambdaQueryWrapper<ApiConfig> queryWrapper = new QueryWrapper<ApiConfig>()
                    .lambda().eq(ApiConfig::getApiKey,apiKey);
            ApiConfig apiConfig = apiConfigService.getOne(queryWrapper);
            if (apiConfig == null){
                throw new IllegalAccessException("系统错误");
            }
            String credential = (signType instanceof RSA2SignType)
                    ?apiConfig.getPubKey():apiConfig.getApiSecurity();
            SignVerify signVerify = signType.sign(credential);
            sign = (signType instanceof RSA2SignType)
                    ?UrlSafeB64.decode(sign):sign;
            if (StringUtils.isEmpty(sign) || !signVerify.verify(sign)){
                throw new IllegalAccessException("签名校验失败");
            }
        } catch (Exception e) {
            response.setStatus(404);
            return null;
        }
        OrderDTO order = orderService.getByOrderNo(orderNo);
        if (order == null || order.getStatus() != 1) {
            response.setStatus(404);
            return null;
        }
        String productCode = order.getPayProductCode();
        if (productCode == null || productCode.length() == 0) {
            response.setStatus(404);
            return null;
        }
        modelMap.put("order",order);
        return String.format("cashier/%s",productCode);
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

    @GetMapping("/pay/wx/order")
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
}
