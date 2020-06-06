package com.esiran.greenpay.pay.plugin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.PayOrder;
import com.esiran.greenpay.pay.service.IMerchantPrepaidAccountService;
import com.esiran.greenpay.pay.service.IOrderService;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ACPayPlugin implements Plugin<PayOrder> {
    private static final Gson g = new Gson();
    private static final OkHttpClient okHttpClient;
    private static RedisDelayQueueClient redisDelayQueueClient;
    private static IOrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(ACPayPlugin.class);
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    public ACPayPlugin(RedisDelayQueueClient redisDelayQueueClient, IOrderService orderService) {
        this.redisDelayQueueClient = redisDelayQueueClient;
        this.orderService = orderService;
    }

    private static final class CreateOrderTask implements Task<PayOrder>{


        @Override
        public String taskName() {
            return "createOrderTask";
        }

        @Override
        public String dependent() {
            return "create";
        }

        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
            logger.info("付款码支付");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String attrJson = orderDetail.getPayInterfaceAttr();
            String upstreamExtra = orderDetail.getUpstreamExtra();
            Map<String, String> attrMap = MapUtil.jsonString2stringMap(attrJson);
            Map<String, String> upMap = MapUtil.jsonString2stringMap(upstreamExtra);
            if (attrMap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            if (upMap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            String memberId = attrMap.get("memberId");
            String apiClientPrivKey = attrMap.get("apiClientPrivKey");
            if (StringUtils.isEmpty(apiClientPrivKey)
                    || StringUtils.isEmpty(apiClientPrivKey)){
                throw new APIException("支付接口参数有误","CHANNEL_REQUEST_ERROR");
            }
            String authCode = upMap.get("authCode");
            if (StringUtils.isEmpty(authCode)){
                throw new APIException("付款码为不能为空","CHANNEL_REQUEST_ERROR");
            }
            Map<String,String> map = new HashMap<>();
            map.put("authCode",authCode);
            map.put("orderAmt",String.valueOf(order.getAmount()));
            map.put("memberId",memberId);
            String principal = MapUtil.sortAndSerialize(map);
            Md5SignType signType = new Md5SignType(principal);
            String sign = signType.sign2(apiClientPrivKey);
            map.put("sign",sign);
            logger.info("sign: {}",sign);
            logger.info("apiPrivKey: {}",apiClientPrivKey);
            logger.info("Request: {}",MapUtil.sortAndSerialize(map));
            String json = g.toJson(map);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request request = new Request.Builder()
                    .url("http://api.acpay.leyyx.cn/alipayCreditPay/payFromOutside")
                    .post(requestBody)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                String body = responseBody.string();
                logger.info("Response body: {}", body);
                Map<String, Object> objectMap = MapUtil.jsonString2objMap(body);
                if (objectMap == null) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                String result = (String) objectMap.get("result");
                String msg = (String) objectMap.get("msg");
                if (result.equals("paying")){
                    Map<String,String> messagePayload = new HashMap<>();
                    messagePayload.put("orderNo", order.getOrderNo());
                    messagePayload.put("count", "1");
                    redisDelayQueueClient.sendDelayMessage("order:acpay",g.toJson(messagePayload),5*1000);
                }else if (result.equals("success")){
                    LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.set(Order::getStatus,2)
                            .set(Order::getPaidAt, LocalDateTime.now())
                            .eq(Order::getOrderNo,order.getOrderNo());
                    orderService.update(wrapper);
                    Map<String,String> messagePayload = new HashMap<>();
                    messagePayload.put("orderNo", order.getOrderNo());
                    messagePayload.put("mchId", String.valueOf(order.getMchId()));
                    messagePayload.put("count", "1");
                    redisDelayQueueClient.sendDelayMessage("order:notify",g.toJson(messagePayload),0);
                }else if (result.equals("fail")){
                    throw new APIException(msg,"CHANNEL_REQUEST_ERROR");
                }

//                Map<String,String> authCodemap = new HashMap<>();
//                authCodemap.put("authCode",authCode);
//                String s = g.toJson(authCodemap);
//                RequestBody selectRequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
//                Request selectRequest = new Request.Builder()
//                        .url("http://localhost:8601/alipayCreditPay/selectPayResult")
//                        .post(selectRequestBody)
//                        .build();
//                Response selectResponse = okHttpClient.newCall(selectRequest).execute();
//                ResponseBody selectResponseBody = selectResponse.body();
//                if (selectResponseBody == null) {
//                    //TODO
//                    throw new ApiException("");
//                }
//                String string = selectResponseBody.string();
//                logger.info("Response selectResponseBody: {}", string);
////                if (StringUtils.isEmpty(body)) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
////                Pattern pattern = Pattern.compile("^<form .+$",Pattern.DOTALL);
////                Matcher matcher = pattern.matcher(body);
////                body = new String(Base64.getEncoder().encode(body.getBytes(StandardCharsets.UTF_8)));
////                if (!matcher.matches()) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
//                Map<String,Object> objectMap = new HashMap<>();
//                objectMap.put("resultUrl",null);
//                objectMap.put("resultBody",body);
//                flow.returns(objectMap);
            }
        }
    }
    private static final class OrderNotifyTask implements Task<PayOrder> {

        @Override
        public String taskName() {
            return "orderNotifyTask";
        }

        @Override
        public String dependent() {
            return "notify";
        }

        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
        }
    }

    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
    }
}
