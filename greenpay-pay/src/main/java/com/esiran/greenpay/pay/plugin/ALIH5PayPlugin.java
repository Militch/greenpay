package com.esiran.greenpay.pay.plugin;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppMergePayRequest;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.PayOrder;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 阿里H5扫码支付
 */
@Component
public class ALIH5PayPlugin implements Plugin<PayOrder> {
    private static final Gson g = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(UpacpQrJKPlugin.class);
    public static final class CreateOrderTask implements Task<PayOrder>{

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
            System.out.println("支付宝H5支付");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
            String authCode = orderDetail.getUpstreamExtra();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(payInterfaceAttr);
            if (attrmap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            if (StringUtils.isEmpty(attrmap.get("appId"))
                    || StringUtils.isEmpty(attrmap.get("privateKey"))
                    || StringUtils.isEmpty(attrmap.get("publicKey"))){
                throw new APIException("支付接口参数有误","CHANNEL_REQUEST_ERROR");
            }
            DefaultAlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                    attrmap.get("appId"),
                    attrmap.get("privateKey"),
                    "json",
                    "UTF-8",
                    attrmap.get("publicKey"),
                    "RSA2");
            AlipayTradeAppMergePayRequest request = new AlipayTradeAppMergePayRequest();
            request.setNotifyUrl(order.getNotifyUrl());
            request.setReturnUrl("");
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("out_trade_no",order.getOrderNo());
            requestMap.put("product_code","FAST_INSTANT_TRADE_PAY");
            requestMap.put("total_amount", NumberUtil.amountFen2Yuan(order.getAmount()));
            requestMap.put("subject",order.getSubject());
            String requestMsg = g.toJson(requestMap);
            request.setBizContent(requestMsg);
            String form = "";
            try {
                form = alipayClient.pageExecute(request).getBody();
                Map<String, Object> map = new HashMap<>();
                map.put("form",form);
                flow.returns(map);
            } catch (AlipayApiException e) {
                e.printStackTrace();
                throw new APIException(e.getMessage(),"");
            }
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
    }
}
