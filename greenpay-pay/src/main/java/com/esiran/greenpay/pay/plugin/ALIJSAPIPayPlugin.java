package com.esiran.greenpay.pay.plugin;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeCancelModel;
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
 * 阿里JSAPI支付
 */
@Component
public class ALIJSAPIPayPlugin implements Plugin<PayOrder> {
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
            System.out.println("支付宝JSAPI支付");
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
            AlipayTradeAppMergePayRequest request = new AlipayTradeAppMergePayRequest();
            request.setNotifyUrl(order.getNotifyUrl());
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setBody(order.getSubject());
            model.setOutTradeNo(order.getOrderNo());
            model.setTotalAmount(NumberUtil.amountFen2Yuan(order.getAmount()));
            model.setTimeoutExpress("90m");
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            String form = "";
            try {
                form = alipayClient.sdkExecute(request).getBody();
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
