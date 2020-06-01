package com.esiran.greenpay.pay.plugin;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppMergePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppMergePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 阿里当面付扫码付款
 */
@Component
public class ALIQrPayPlugin implements Plugin<PayOrder> {
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
            System.out.println("支付宝扫码支付");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
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
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("out_trade_no",order.getOrderNo());
            requestMap.put("subject",order.getSubject());
            requestMap.put("total_amount", NumberUtil.amountFen2Yuan(order.getAmount()));
            requestMap.put("timeout_express","90m");
            String requestMsg = g.toJson(requestMap);
            request.setBizContent(requestMsg);
            AlipayTradePrecreateResponse execute = alipayClient.execute(request);
            String res = execute.getBody();
            Map<String, Object> map = MapUtil.jsonString2objMap(res);
            if (map != null){
                Map<String,String> resultMap = (Map<String, String>) map.get("alipay_trade_precreate_response");
                if (resultMap.get("msg").equals("Success")){
                    Map<String,Object> returns = new HashMap<>();
                    returns.put("codeUrl",resultMap.get("qr_code"));
                    flow.returns(returns);
                }
            }
        }
    }
    public static final class NotifyOrderTask implements Task<PayOrder>{

        @Override
        public String taskName() {
            return "createOrderTask";
        }

        @Override
        public String dependent() {
            return "notify";
        }

        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
            System.out.println("支付宝扫码支付回调");

        }
    }
    public static final class QueryOrderTask implements Task<PayOrder>{
        @Override
        public String taskName() {
            return "createOrderTask";
        }

        @Override
        public String dependent() {
            return "query";
        }

        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
            System.out.println("支付宝扫码订单查询");
            PayOrder payOrder = flow.getData();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
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
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("out_trade_no",orderDetail.getOrderNo());
            String requestMsg = g.toJson(requestMap);
            request.setBizContent(requestMsg);
            AlipayTradeQueryResponse execute = alipayClient.execute(request);
            String res = execute.getBody();
            Map<String, Object> map = MapUtil.jsonString2objMap(res);
            Map<String,Object> returns = new HashMap<>();
            if (map != null){
                Map<String,Object> resultMap = (Map<String,Object>) map.get("alipay_trade_query_response");
                if (resultMap.get("msg").equals("Success")){
                    String status = (String) resultMap.get("trade_status");
                    if (status.equals("WAIT_BUYER_PAY")){
                        returns.put("status","paying");
                    }else if (status.equals("TRADE_SUCCESS")){
                        returns.put("status","payend");
                    }
                    flow.returns(returns);
                }
            }
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
        flow.add(new QueryOrderTask());
    }
}
