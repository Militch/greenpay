package com.esiran.greenpay.pay.plugin;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppMergePayRequest;
import com.alipay.api.response.AlipayTradeAppMergePayResponse;
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
 * 阿里当面付 付款码付款
 */
@Component
public class ALIScanPayPlugin implements Plugin<PayOrder> {
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
            System.out.println("支付宝付款码支付");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
            String authCode = orderDetail.getUpstreamExtra();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(payInterfaceAttr);
            if (attrmap == null) throw new APIException("请求参数有误", "CHANNEL_REQUEST_ERROR");
            if (StringUtils.isEmpty(attrmap.get("appId"))
                    || StringUtils.isEmpty(attrmap.get("privateKey"))
                    || StringUtils.isEmpty(attrmap.get("publicKey"))
                    || StringUtils.isEmpty("storeId")) {
                throw new APIException("支付接口参数有误", "CHANNEL_REQUEST_ERROR");
            }
            DefaultAlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                    attrmap.get("appId"),
                    attrmap.get("privateKey"),
                    "json",
                    "UTF-8",
                    attrmap.get("publicKey"),
                    "RSA2");
            AlipayTradeAppMergePayRequest request = new AlipayTradeAppMergePayRequest();
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("out_trade_no", order.getOrderNo());
            requestMap.put("scene", "bar_code");
            requestMap.put("auth_code", authCode);
            requestMap.put("subject", order.getSubject());
            requestMap.put("store_id", attrmap.get("storeId"));
            requestMap.put("total_amount", NumberUtil.amountFen2Yuan(order.getAmount()));
            requestMap.put("timeout_express", "90m");
            String requestMsg = g.toJson(requestMap);
            request.setBizContent(requestMsg);
            AlipayTradeAppMergePayResponse execute = alipayClient.execute(request);
            String res = execute.getBody();
            Map<String, Object> map = MapUtil.jsonString2objMap(res);
            flow.returns(map);
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
    }
}
