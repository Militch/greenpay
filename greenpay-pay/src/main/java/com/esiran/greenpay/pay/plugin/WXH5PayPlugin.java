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
import com.esiran.greenpay.pay.entity.WxMyConfig;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信H5扫码支付
 */
@Component
public class WXH5PayPlugin implements Plugin<PayOrder> {
    private static final Gson g = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(WXH5PayPlugin.class);
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
            logger.info("微信H5支付");
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
            data.put("trade_type", "MWEB");//交易类型
            String s = WXPayUtil.generateSignature(data,wxMyConfig.getMchKey());  //签名
            data.put("sign", s);//签名
            try {
                logger.info("sign{}",data.get("sign"));
                //使用官方API请求预付订单
                Map<String, String> response = wxPay.unifiedOrder(data);
                logger.info("微信H5支付  返回值 : {}",g.toJson(response));
                String returnCode = response.get("return_code");//获取返回码
                logger.info("返回码{}",returnCode); //获取返回码
                //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
                if (returnCode.equals("SUCCESS")) {
                    String mweb_url = response.get("mweb_url");
                    logger.info("mweb_url : {}",mweb_url);
                    Map<String,Object> returns = new HashMap<>();
                    returns.put("url",mweb_url);
                    flow.returns(returns);
                } else {
                    throw new APIException("","");
                }
            } catch (Exception e) {
                throw new APIException(e.getMessage(),"");
            }
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
    }
}
