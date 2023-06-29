package com.esiran.greenpay.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.PluginLoader;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.exception.ResourceNotFoundException;
import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.sign.SignType;
import com.esiran.greenpay.common.sign.SignVerify;
import com.esiran.greenpay.common.util.*;
import com.esiran.greenpay.merchant.entity.ApiConfig;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.openapi.entity.Invoice;
import com.esiran.greenpay.openapi.entity.InvoiceInputDTO;
import com.esiran.greenpay.openapi.entity.InvoiceQueryInputDTO;
import com.esiran.greenpay.openapi.service.IInvoiceService;
import com.esiran.greenpay.pay.entity.*;
import com.esiran.greenpay.pay.plugin.PayOrderFlow;
import com.esiran.greenpay.pay.service.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class InvoiceService implements IInvoiceService {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final Gson gson = new GsonBuilder().create();
    private final IMerchantService merchantService;
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    private final IdWorker idWorker;
    private final IInterfaceService interfaceService;
    private final IProductService productService;
    //private final RedisDelayQueueClient redisDelayQueueClient;
    private final PluginLoader pluginLoader;
    @Value("${greenpay.web.hostname:http://localhost}")
    private String webHostname;
    public InvoiceService(
            IMerchantService merchantService,
            IOrderService orderService,
            IOrderDetailService orderDetailService,
            IdWorker idWorker,
            IInterfaceService interfaceService,
            IProductService productService,
            PluginLoader pluginLoader, IApiConfigService apiConfigService) {
        this.merchantService = merchantService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.idWorker = idWorker;
        this.interfaceService = interfaceService;
        this.productService = productService;
        this.pluginLoader = pluginLoader;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice createInvoiceByInput(InvoiceInputDTO invoiceInputDTO, Merchant merchant) throws Exception {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PayOrder payOrder = createPretreatmentInvoice(invoiceInputDTO,merchant);
        Order order = payOrder.getOrder();
        OrderDetail orderDetail = payOrder.getOrderDetail();
        Map<String,Object> results = null;
        Product product = productService.getById(order.getPayProductId());
        Interface ins = interfaceService.getById(orderDetail.getPayInterfaceId());
        try {
            Plugin<PayOrder> plugin = pluginLoader.loadForClassPath(ins.getInterfaceImpl());
            PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
            plugin.apply(payOrderFlow);
            payOrderFlow.execDependent("create");
            results = payOrderFlow.getResults();
            orderDetailService.updatePayCredentialByOrderNo(order.getOrderNo(),results);
        }catch (Exception e){
            e.printStackTrace();
            throw new APIException("CHANNEL_REQUEST_ERROR",e.getMessage());
        }
        Invoice out = modelMapper.map(order,Invoice.class);
        out.setChannel(product.getProductCode());
        out.setCredential(results);
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrder createPretreatmentInvoice(InvoiceInputDTO invoiceInputDTO, Merchant merchant) throws APIException {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Invoice invoice = modelMapper.map(invoiceInputDTO,Invoice.class);
        invoice.setOrderNo(String.valueOf(idWorker.nextId()));
        invoice.setOrderSn(EncryptUtil.baseTimelineCode());
        invoice.setCreatedAt(LocalDateTime.now());
        if (!merchant.getStatus()){
            throw new APIException("商户状态已锁定","MERCHANT_STATUS_LOCKED");
        }
        Product product = productService.getByProductCode(invoice.getChannel());
        if (product == null || !product.getStatus()) {
            throw new APIException("支付产品暂不支持创建订单","PRODUCT_NOT_SUPPORT");
        }
        PassageAndSubAccount passageAndSubAccount =
                merchantService.scheduler(merchant.getId(),product.getId());
        if (passageAndSubAccount == null){
            throw new APIException("支付产品暂不支持创建订单","PRODUCT_NOT_SUPPORT");
        }
        Passage passage = passageAndSubAccount.getPassage();
        PassageAccount passageAccount = passageAndSubAccount.getPassageAccount();
        String insCode = passage.getInterfaceCode();
        Interface ins = interfaceService.getByCode(insCode);
        if (ins == null || !ins.getStatus()){
            throw new APIException("支付产品暂不支持创建订单","PRODUCT_NOT_SUPPORT");
        }
        logger.info("Create order by Product Id: {}, Passage Id: {}, Passage Account: {}",
                product.getId(), passage.getId(), passageAccount.getId());
        // 构造订单
        Order order = modelMapper.map(invoice,Order.class);
        order.setStatus(1);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setMchId(merchant.getId());
        order.setPayProductId(product.getId());
        Integer orderFee = NumberUtil.calculateAmountFee(order.getAmount(),
                passageAndSubAccount.getProductRate());
        order.setFee(orderFee);
        order.setPayProductName(product.getProductName());
        order.setPayProductCode(product.getProductCode());
        LambdaQueryWrapper<Order> orderQueryWrapper = new QueryWrapper<Order>().lambda()
                .eq(Order::getMchId,order.getMchId())
                .eq(Order::getAppId,order.getAppId())
                .eq(Order::getOutOrderNo,order.getOutOrderNo());
        Order oldOrder = orderService.getOne(orderQueryWrapper);
        if (oldOrder != null){
            throw new APIException("商户订单号不能重复","ORDER_NO_DUPLICATE");
        }
        orderService.save(order);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setPayTypeCode(product.getPayTypeCode());
        orderDetail.setOrderNo(invoice.getOrderNo());
        orderDetail.setPayProductId(product.getId());
        orderDetail.setPayPassageId(passage.getId());
        orderDetail.setPayPassageAccId(passageAccount.getId());
        orderDetail.setPayInterfaceAttr(passageAccount.getInterfaceAttr());
        orderDetail.setPayInterfaceId(ins.getId());
        String channelExtraStr = invoiceInputDTO.getChannelExtra();
        orderDetail.setUpstreamExtra(channelExtraStr);
        orderDetail.setCreatedAt(LocalDateTime.now());
        orderDetail.setUpdatedAt(LocalDateTime.now());
        orderDetailService.save(orderDetail);
        PayOrder payOrder = new PayOrder();
        payOrder.setOrder(order);
        payOrder.setOrderDetail(orderDetail);
        String notifyReceiveUrl = String.format(
                "%s/v1/invoices/%s/callback",
                webHostname,order.getOrderNo());
        payOrder.setNotifyReceiveUrl(notifyReceiveUrl);
        //redisDelayQueueClient.sendDelayMessage("order:expire",order.getOrderNo(),20*60*1000);
        return payOrder;
    }

    @Override
    public String handleInvoiceCallback(HttpServletRequest request, HttpServletResponse response, String orderNo) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Order order = orderService.getOneByOrderNo(orderNo);
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (order == null || orderDetail == null || order.getStatus() != 1){
            response.setStatus(404);
            return null;
        }
        PayOrder payOrder = new PayOrder();
        payOrder.setOrder(order);
        payOrder.setOrderDetail(orderDetail);
        Interface ins = interfaceService.getById(orderDetail.getPayInterfaceId());
        PayOrderFlow payOrderFlow = new PayOrderFlow(payOrder);
        try {
            Plugin<PayOrder> plugin = pluginLoader.loadForClassPath(ins.getInterfaceImpl());
            payOrderFlow.setRequest(request);
            plugin.apply(payOrderFlow);
            payOrderFlow.execDependent("notify");
            if (!payOrderFlow.isChecked()){
                return payOrderFlow.getFailedString();
            }
            orderService.updateOrderStatus(orderNo,2);
            Map<String,String> messagePayload = new HashMap<>();
            messagePayload.put("orderNo", orderNo);
            messagePayload.put("mchId", String.valueOf(order.getMchId()));
            messagePayload.put("count", "1");
            //redisDelayQueueClient.sendDelayMessage("order:notify",gson.toJson(messagePayload),0);
        } catch (Exception e) {
            return payOrderFlow.getFailedString();
        }
        return payOrderFlow.getSuccessfulString();
    }

    @Override
    public Invoice getOneByQuery(InvoiceQueryInputDTO queryInputDTO) throws ResourceNotFoundException {
        if (StringUtils.isEmpty(queryInputDTO.getOrderNo()) && StringUtils.isEmpty(queryInputDTO.getOutOrderNo()))
            throw new IllegalArgumentException("交易流水号与商户订单号不能同时为空");
        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(queryInputDTO.getOrderNo())){
            lambdaQueryWrapper.eq(Order::getOrderNo,queryInputDTO.getOrderNo()).eq(Order::getAppId, queryInputDTO.getAppId());
        }else if(!StringUtils.isEmpty(queryInputDTO.getOutOrderNo())){
            lambdaQueryWrapper.eq(Order::getOutOrderNo,queryInputDTO.getOutOrderNo()).eq(Order::getAppId, queryInputDTO.getAppId());
        }
        Order order = orderService.getOne(lambdaQueryWrapper);
        if (order == null)
            throw new ResourceNotFoundException("查询无结果");
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(order.getOrderNo());
        if (orderDetail == null)
            throw new ResourceNotFoundException("系统错误");

        Invoice out = modelMapper.map(order,Invoice.class);
        out.setChannel(order.getPayProductCode());
        Map<String,Object> credential = null;
        Map<String,Object> channelExtra = null;
        if (!Strings.isEmpty(orderDetail.getPayCredential())){
            credential = gson.fromJson(orderDetail.getPayCredential(),new TypeToken<Map<String,Object>>(){}.getType());
        }
        if (!StringUtils.isEmpty(orderDetail.getUpstreamExtra())){
            channelExtra = gson.fromJson(orderDetail.getUpstreamExtra(),new TypeToken<Map<String,Object>>(){}.getType());
        }
        out.setCredential(credential);
        out.setChannelExtra(channelExtra);
        return out;
    }
}
