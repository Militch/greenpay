package com.esiran.greenpay.pay.plugin;

import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.PayOrder;
import com.esiran.greenpay.pay.entity.WxMyConfig;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信APP支付
 */
@Component
public class WXAPPPayPlugin implements Plugin<PayOrder> {
    private static final Gson g = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(WXAPPPayPlugin.class);
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
            logger.info("微信APP支付");
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
            data.put("trade_type", "APP");//交易类型
            String s = WXPayUtil.generateSignature(data,wxMyConfig.getMchKey());  //签名
            data.put("sign", s);//签名
            try {
                logger.info("sign{}",data.get("sign"));
                //使用官方API请求预付订单
                Map<String, String> response = wxPay.unifiedOrder(data);
                logger.info("微信APP支付  返回值 : {}",g.toJson(response));
                String returnCode = response.get("return_code");//获取返回码
                logger.info("返回码{}",returnCode); //获取返回码
                //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
                if (returnCode.equals("SUCCESS") && response.get("result_code").equals("SUCCESS")) {
                    Map<String,String> parameterMap = new HashMap<>();
                    parameterMap.put("appid",response.get("appid"));
                    parameterMap.put("partnerid",response.get("partnerid"));
                    parameterMap.put("prepayid",response.get("prepayid"));
                    parameterMap.put("package",response.get("Sign=WXPay"));
                    parameterMap.put("noncestr",response.get("nonce_str"));
                    parameterMap.put("timestamp",String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));
                    String signature = WXPayUtil.generateSignature(parameterMap, wxMyConfig.getMchKey());
                    parameterMap.put("sign",signature);
                    HashMap<String, Object> returns = new HashMap<>();
                    returns.put("result",parameterMap);
                    flow.returns(returns);
                } else {
                    throw new APIException("","");
                }
            } catch (Exception e) {
                throw new APIException(e.getMessage(),"");
            }
        }
    }

    private static final class HandleOrderNotifyCheckTask implements Task<PayOrder> {
        private static final Gson gson = new Gson();
        @Override
        public String taskName() {
            return "handleOrderNotifyCheck";
        }

        @Override
        public String dependent() {
            return "notify";
        }
        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
            flow.setSuccessfulString(WxPayNotifyResponse.success("处理成功!"));
            flow.setFailedString(WxPayNotifyResponse.success("处理失败!"));
            HttpServletRequest request = (HttpServletRequest) flow.getRequest();
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String resultxml = new String(outSteam.toByteArray(), "utf-8");
            outSteam.close();
            inStream.close();
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
            Map<String, String> req = WXPayUtil.xmlToMap(resultxml);
            String wxsign = req.remove("sign");
            String sign = WXPayUtil.generateSignature(req, wxMyConfig.getMchKey());
            HashMap<String, String> result = new HashMap<>();
            if (wxsign.equals(sign)){
                if (req.get("return_code").equals("SUCCESS")){
                    result.put("return_code","SUCCESS");
                    result.put("return_msg","OK");
                    String toXml = WXPayUtil.mapToXml(result);
                    flow.setSuccessfulString(toXml);
                }else {
                    result.put("return_code","FAIL");
                    result.put("return_msg","return_code不正确");
                    String toXml = WXPayUtil.mapToXml(result);
                    flow.setFailedString(WxPayNotifyResponse.success(toXml));
                    flow.setChecked(false);
                }
            }else {
                result.put("return_code","FAIL");
                result.put("return_msg","签名失败");
                String toXml = WXPayUtil.mapToXml(result);
                flow.setFailedString(WxPayNotifyResponse.success(toXml));
                flow.setChecked(false);
            }
            flow.setChecked(true);
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
    }
}
