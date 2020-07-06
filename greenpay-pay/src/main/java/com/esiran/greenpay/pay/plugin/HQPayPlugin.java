package com.esiran.greenpay.pay.plugin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.esiran.greenpay.pay.service.IOrderService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 *环球支付
 */
@Component
public class HQPayPlugin implements Plugin<PayOrder> {
    private static final Gson g = new Gson();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final OkHttpClient okHttpClient;
    private static RedisDelayQueueClient redisDelayQueueClient;
    private static IOrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(HQPayPlugin.class);
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    public HQPayPlugin(RedisDelayQueueClient redisDelayQueueClient, IOrderService orderService) {
        this.redisDelayQueueClient = redisDelayQueueClient;
        this.orderService = orderService;
    }

    private static final class CreateOrderTask implements Task<PayOrder> {


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
            logger.info("环球支付");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String attrJson = orderDetail.getPayInterfaceAttr();
            Map<String, String> attrMap = MapUtil.jsonString2stringMap(attrJson);
            if (attrMap == null) throw new APIException("支付接口参数有误", "CHANNEL_REQUEST_ERROR");
            String pay_memberid = attrMap.get("memberid");
            String apiClientPrivKey = attrMap.get("apiClientPrivKey");
            if (StringUtils.isEmpty(pay_memberid) || StringUtils.isEmpty(apiClientPrivKey)) {
                throw new APIException("支付接口参数有误", "CHANNEL_REQUEST_ERROR");
            }
            Map<String, String> map = new HashMap<>();
            map.put("pay_memberid", pay_memberid);
            map.put("pay_orderid", order.getOrderNo());
            map.put("pay_applydate", dtf.format(order.getCreatedAt()));
            map.put("pay_bankcode", "");
            map.put("pay_notifyurl", payOrder.getNotifyReceiveUrl());
            map.put("pay_callbackurl", "");
            map.put("pay_amount", String.valueOf(order.getAmount()));
            map.put("pay_productname", order.getSubject());
            String principal = MapUtil.sortAndSerialize(map);
            String concat = principal.concat("&key=" + apiClientPrivKey);
            Md5SignType signType = new Md5SignType(concat);
            String sign = signType.sign2(null);
            map.put("pay_md5sign", sign);
            logger.info("sign: {}", sign);
            logger.info("Request: {}", MapUtil.sortAndSerialize(map));
            String json = g.toJson(map);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request request = new Request.Builder()
                    .url("http://pay.nbz8888.com/Pay_Index.html")
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String s = response.body().string();
            System.out.println(s);
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
            flow.setSuccessfulString(WxPayNotifyResponse.success("OK"));
            flow.setFailedString(WxPayNotifyResponse.success("OK"));
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String configJson = orderDetail.getPayInterfaceAttr();
            Map<String,String> attrMap = MapUtil.jsonString2stringMap(configJson);
            if (attrMap == null) throw new APIException("支付接口参数有误", "CHANNEL_REQUEST_ERROR");
            String pay_memberid = attrMap.get("pay_memberid");
            String apiClientPrivKey = attrMap.get("apiClientPrivKey");
            if (StringUtils.isEmpty(pay_memberid) || StringUtils.isEmpty(apiClientPrivKey)) {
                throw new APIException("支付接口参数有误", "CHANNEL_REQUEST_ERROR");
            }
            String request = (String) flow.getRequest();
            Map<String, String> map = MapUtil.jsonString2stringMap(request);
            assert map != null;
            map.remove("sign");
            String s = MapUtil.sortAndSerialize(map);
            String concat = s.concat("&key=" + apiClientPrivKey);
            Md5SignType signType = new Md5SignType(concat);
            String sign = signType.sign2(null);
            if (sign.equals(map.get("sign"))){
                if(map.get("returncode").equals("00")){
                    //支付成功，写返回数据逻辑
                    LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.set(Order::getStatus,2)
                            .set(Order::getPaidAt, LocalDateTime.now())
                            .eq(Order::getOrderNo,order.getOrderNo());
                    orderService.update(wrapper);
                    flow.setChecked(true);
                }else{
                    LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.set(Order::getStatus,-2)
                            .set(Order::getPaidAt, LocalDateTime.now())
                            .eq(Order::getOrderNo,order.getOrderNo());
                    orderService.update(wrapper);
                    flow.setChecked(true);
                }
            }else {
                throw new APIException("验签失败","");
            }
            flow.setChecked(true);

        }
    }

    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
        flow.add(new OrderNotifyTask());
    }
}
