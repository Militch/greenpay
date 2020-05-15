package com.esiran.greenpay.admin.runner;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.message.delayqueue.DelayQueueTaskRunner;
import com.esiran.greenpay.message.delayqueue.impl.RedisDelayQueueClient;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.service.IOrderDetailService;
import com.esiran.greenpay.pay.service.IOrderService;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Component
public class OrderACPayTaskRunner implements DelayQueueTaskRunner {
    private final Logger logger = LoggerFactory.getLogger(OrderACPayTaskRunner.class);
    private final IOrderService orderService;
    private static final OkHttpClient okHttpClient;
    private final Gson g = new Gson();
    private final IOrderDetailService orderDetailService;
    private final RedisDelayQueueClient redisDelayQueueClient;
    public OrderACPayTaskRunner(IOrderService orderService, IOrderDetailService orderDetailService, RedisDelayQueueClient redisDelayQueueClient) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.redisDelayQueueClient = redisDelayQueueClient;
    }

    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }

    @Override
    public void exec(String content) {
        OrderDetail orderDetail = orderDetailService.getOneByOrderNo(content);
        String extra = orderDetail.getUpstreamExtra();
        RequestBody selectRequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), extra);
        Request selectRequest = new Request.Builder()
                .url("http://localhost:8601/alipayCreditPay/selectPayResult")
                .post(selectRequestBody)
                .build();
        Response selectResponse = null;
        try {
            selectResponse = okHttpClient.newCall(selectRequest).execute();
            ResponseBody selectResponseBody = selectResponse.body();
            if (selectResponseBody == null) {
                LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
                wrapper.set(Order::getStatus,-2)
                        .set(Order::getUpdatedAt, LocalDateTime.now())
                        .eq(Order::getOrderNo,content);
                orderService.update(wrapper);
            }
            String string = selectResponseBody.string();
            logger.info("Response selectResponseBody: {}", string);
            Map<String, Object> map = MapUtil.jsonString2objMap(string);
            ArrayList<Map<String,Object>> arrayList = (ArrayList) map.get("data");
            Map<String, Object> stringObjectMap = arrayList.get(0);
            String payStatus = (String) stringObjectMap.get("payStatus");
            if (payStatus.equals("paying")){
                redisDelayQueueClient.sendDelayMessage("order:acpay",orderDetail.getOrderNo(),5*1000);
            }else if (payStatus.equals("payed")){
                LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
                wrapper.set(Order::getStatus,2)
                        .set(Order::getPaidAt, LocalDateTime.now())
                        .eq(Order::getOrderNo,content);
                orderService.update(wrapper);
                logger.info("Response Redisacpay orderNo: {}", content);
                logger.info("Response Redisacpay payStatus: {}", payStatus);
            }else if (payStatus.equals("notPay") || payStatus.equals("fail") || payStatus.equals("abnormal")){
                LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
                wrapper.set(Order::getStatus,-2)
                        .set(Order::getPaidAt, LocalDateTime.now())
                        .eq(Order::getOrderNo,content);
                orderService.update(wrapper);
                logger.info("Response Redisacpay orderNo: {}", content);
                logger.info("Response Redisacpay payStatus: {}", payStatus);
            }

//            LambdaUpdateWrapper<Order> orderUpdateWrapper = new LambdaUpdateWrapper<>();
//            orderUpdateWrapper.set(Order::getStatus, -1)
//                    .set(Order::getExpiredAt, LocalDateTime.now())
//                    .eq(Order::getOrderNo,content)
//                    .eq(Order::getStatus,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}