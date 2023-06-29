package com.esiran.greenpay.pay.service.impl;

import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.sign.RSA2SignType;
import com.esiran.greenpay.common.sign.SignType;
import com.esiran.greenpay.common.sign.SignVerify;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.UrlSafeB64;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.pay.service.IOrderNotifyService;
import com.esiran.greenpay.pay.service.IOrderService;
import okhttp3.*;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderNotifyService implements IOrderNotifyService {
    private static final OkHttpClient okHttpClient;
    private static final Logger logger = LoggerFactory.getLogger(OrderNotifyService.class);
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    public OrderNotifyService(
            IOrderService orderService,
            IOrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }
    private static FormBody buildFormBody4map(Map<String,String> maps){
        if (maps == null) return null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (String key: maps.keySet()){
            formBodyBuilder.add(key,maps.get(key));
        }
        return formBodyBuilder.build();
    }
    @Override
    public boolean notifyByOrderNo(String orderNo, String credential) throws PostResourceException {
        return notifyByOrderNo(orderNo,credential,"md5");
    }

    @Override
    public boolean notifyByOrderNo(String orderNo, String credential, String sigType) throws PostResourceException {
        Order order = orderService.getOneByOrderNo(orderNo);
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(orderNo);
        if (order == null || orderDetail == null)
            throw new PostResourceException("订单不存在，无法执行回调");
        String notifyUrl = order.getNotifyUrl();
        if (StringUtils.isEmpty(notifyUrl)){
            throw new PostResourceException("订单回调地址为空，无法执行回调");
        }
        if (StringUtils.isEmpty(sigType)){
            throw new PostResourceException("接口签名方式为空，无法执行回调");
        }
        if (StringUtils.isEmpty(credential)){
            throw new PostResourceException("接口安全证书为空，无法执行回调");
        }
        Map<String,String> params = new HashMap<>();
        params.put("orderNo",order.getOrderNo());
        params.put("outOrderNo",order.getOutOrderNo());
        params.put("channel",order.getPayProductCode());
        params.put("subject",order.getSubject() );
        if (order.getBody() != null){
            params.put("body",order.getBody());
        }
        params.put("amount",String.valueOf(order.getAmount()));
        params.put("fee",String.valueOf(order.getFee()));
        params.put("appId",order.getAppId());
        params.put("status",String.valueOf(order.getStatus()));
        params.put("timestamp",String.valueOf(System.currentTimeMillis()));
        params.put("signType",sigType);
        SignType signType = new RSA2SignType(MapUtil.sortAndSerialize(params));
        String sign = signType.sign2(credential);
        sign = UrlSafeB64.encode(sign);
        logger.info("Notify Sign: {}", sign);
        params.put("sign",sign);
        logger.info("sign : {}",sign);
        FormBody formBody = buildFormBody4map(params);
        if (formBody == null)
            throw new PostResourceException("未知错误，无法执行回调");
        Request rq = new Request.Builder()
                .url(notifyUrl)
                .post(formBody)
                .build();
        try (Response response = okHttpClient.newCall(rq).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response.code());
            ResponseBody rb = response.body();
            if (rb == null)
                throw new IllegalArgumentException("返回体为空");
            String body = rb.string();
            logger.debug("Request remote url: {}, body: {}",notifyUrl,body);
            return !StringUtils.isEmpty(body) && body.toLowerCase().equals("success");
        } catch (Exception e) {
            return false;
        }
    }
}
