package com.esiran.greenpay.pay.plugin;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.PayOrder;
import com.esiran.greenpay.pay.entity.WxMyConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 微信扫码支付
 */
@Component
public class WXQrPayPlugin implements Plugin<PayOrder> {
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
            System.out.println("微信扫码支付");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(payInterfaceAttr);
            if (attrmap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            if (StringUtils.isEmpty(attrmap.get("appId"))
                    || StringUtils.isEmpty(attrmap.get("mchId"))
                    || StringUtils.isEmpty(attrmap.get("mchKey"))){
                throw new APIException("支付接口参数有误","CHANNEL_REQUEST_ERROR");
            }
            WxMyConfig wxMyConfig = new WxMyConfig(attrmap.get("appId")
                    , attrmap.get("mchId")
                    , attrmap.get("mchKey"));
            WXPay wxPay = new WXPay(wxMyConfig);
            Map<String, String> data = new HashMap<>();
            data.put("appid", wxMyConfig.getAppID());
            data.put("mch_id", wxMyConfig.getMchID());
            data.put("nonce_str", WXPayUtil.generateNonceStr());
            data.put("body",order.getSubject());
            data.put("out_trade_no", order.getOrderNo());//订单号
            data.put("total_fee", String.valueOf(order.getAmount()));//支付金额
            data.put("spbill_create_ip", "127.0.0.1"); //自己的服务器IP地址
            data.put("notify_url", order.getNotifyUrl());//异步通知地址（请注意必须是外网）
            data.put("trade_type", "NATIVE");//交易类型
            String s = WXPayUtil.generateSignature(data,wxMyConfig.getMchKey());  //签名
            data.put("sign", s);//签名
            try {
                logger.info("sign{}",data.get("sign"));
                //使用官方API请求预付订单
                Map<String, String> response = wxPay.unifiedOrder(data);
                String returnCode = response.get("return_code");//获取返回码
                logger.info("返回码{}",returnCode); //获取返回码
                //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
                if (returnCode.equals("SUCCESS")) {
                    String code_url = response.get("code_url");
                    logger.info("code_url : {}",code_url);
                    Map<String,Object> returns = new HashMap<>();
                    returns.put("codeUrl",code_url);
                    flow.returns(returns);
                } else {
                    throw new APIException("","");
                }
            } catch (Exception e) {
                throw new APIException(e.getMessage(),"");
            }
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
            System.out.println("微信扫码订单查询");
            PayOrder payOrder = flow.getData();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(payInterfaceAttr);
            if (attrmap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            if (StringUtils.isEmpty(attrmap.get("appId"))
                    || StringUtils.isEmpty(attrmap.get("mchId"))
                    || StringUtils.isEmpty(attrmap.get("mchKey"))){
                throw new APIException("支付接口参数有误","CHANNEL_REQUEST_ERROR");
            }
            WxMyConfig wxMyConfig = new WxMyConfig(attrmap.get("appId")
                    , attrmap.get("mchId")
                    , attrmap.get("mchKey"));
            WXPay wxPay = new WXPay(wxMyConfig);
            Map<String, String> data = new HashMap<>();
            data.put("appid", wxMyConfig.getAppID());
            data.put("mch_id", wxMyConfig.getMchID());
            data.put("out_trade_no",orderDetail.getOrderNo());
            data.put("nonce_str", WXPayUtil.generateNonceStr());
            String s = WXPayUtil.generateSignature(data,wxMyConfig.getMchKey());  //签名
            data.put("sign", s);//签名
            try {
                Map<String,Object> returns = new HashMap<>();
                logger.info("sign{}",data.get("sign"));
                //使用官方API请求预付订单
                Map<String, String> response = wxPay.orderQuery(data);
                String returnCode = response.get("trade_state");//获取返回码
                logger.info("返回码{}",returnCode); //获取返回码
                String status =  response.get("trade_state");
                if (status.equals("SUCCESS")){
                    returns.put("status","payend");
                }
                flow.returns(returns);
            } catch (Exception e) {
                throw new APIException(e.getMessage(),"");
            }
        }
    }


    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
        flow.add(new QueryOrderTask());
    }
}
