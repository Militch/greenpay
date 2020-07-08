package com.esiran.greenpay.pay.plugin;

import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.sign.SignType;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.PayOrder;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class QyefdAliH5Plugin implements Plugin<PayOrder> {
    private static final Logger logger = LoggerFactory.getLogger(QyefdAliH5Plugin.class);
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
            flow.setSuccessfulString("OK");
            flow.setFailedString("FAILED");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String configJson = orderDetail.getPayInterfaceAttr();
            Map<String,Object> config = MapUtil.jsonString2objMap(configJson);
            if (config == null) throw new Exception("支付接口参数不能为空");
            WxPayService service = new WxPayServiceImpl();
            HttpServletRequest request = (HttpServletRequest) flow.getRequest();
            String memberId = request.getParameter("memberid");
            String orderId = request.getParameter("orderid");
            String amount = request.getParameter("amount");
            String transactionId = request.getParameter("transaction_id");
            String datetime = request.getParameter("datetime");
            String returncode = request.getParameter("returncode");
            if (!config.get("memberId").equals(memberId)){
                flow.setChecked(false);
                return;
            }
            if (!order.getOrderNo().equals(orderId)){
                flow.setChecked(false);
                return;
            }
            double amountD = Double.parseDouble(amount);
            double orderAmount = Double.parseDouble(NumberUtil.amountFen2Yuan(order.getAmount()));
            if (amountD != orderAmount){
                flow.setChecked(false);
                return;
            }
            if (!returncode.equals("00")){
                flow.setChecked(false);
                return;
            }
            flow.setChecked(true);
        }
    }


    private static final class CreatePayOrderTask  implements Task<PayOrder> {

        private static String buildFormHtml(String apiGetWay, Map<String,String> attrs){
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html lang=\"zh-CN\">\n");
            sb.append("<head>\n");
            sb.append("    <meta charset=\"UTF-8\">\n");
            sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            sb.append("    <title>正在跳转中...</title>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<body>\n");
            sb.append("<form method=\"post\" action=\"").append(apiGetWay).append("\">\n");
            for (String key: attrs.keySet()){
                sb.append("<input type=\"hidden\" name=\"").append(key).append("\" value=\"")
                        .append(attrs.get(key)).append("\">\n");
            }
            sb.append("</form>\n");
            sb.append("<script>document.forms[0].submit();</script>\n");
            sb.append("</body>\n");
            sb.append("</html>");
            return sb.toString();
        }

        @Override
        public String taskName() {
            return "createPayOrderTask";
        }

        @Override
        public String dependent() {
            return "create";
        }

        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
            logger.info("QyefdAliH5Plugin Start");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String payInterfaceAttr = orderDetail.getPayInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(payInterfaceAttr);
            if (attrmap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            if (StringUtils.isEmpty(attrmap.get("apiGetWay"))
                    || StringUtils.isEmpty(attrmap.get("memberId"))
                    || StringUtils.isEmpty(attrmap.get("apiKey"))){
                throw new APIException("支付接口参数有误","CHANNEL_REQUEST_ERROR");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String applyDate = sdf.format(Calendar.getInstance().getTime());
            Map<String,String> htmlAttrMap = new HashMap<>();
            htmlAttrMap.put("pay_memberid",attrmap.get("memberId"));
            htmlAttrMap.put("pay_orderid",order.getOrderNo());
            htmlAttrMap.put("pay_applydate",applyDate);
            htmlAttrMap.put("pay_bankcode","1053");
            htmlAttrMap.put("pay_notifyurl",payOrder.getNotifyReceiveUrl());
            htmlAttrMap.put("pay_callbackurl","http://baidu.com");
            htmlAttrMap.put("pay_amount",NumberUtil.amountFen2Yuan(order.getAmount()));
            htmlAttrMap.put("pay_productname",order.getSubject());
            htmlAttrMap.put("pay_productnum","");
            htmlAttrMap.put("pay_productdesc","");
            htmlAttrMap.put("pay_producturl","");
            String principal = MapUtil.sortAndSerialize(htmlAttrMap,
                    new String[]{"pay_productname","pay_productnum","pay_productdesc","pay_producturl"});
            SignType signType = new Md5SignType(principal);
            String sign = signType.sign2("key=".concat(attrmap.get("apiKey")));
            logger.info("md5 result: {}", sign.toUpperCase());
            htmlAttrMap.put("pay_md5sign",sign.toUpperCase());
            String html = buildFormHtml(attrmap.get("apiGetWay"),htmlAttrMap);
            Map<String,Object> to = new HashMap<>();
            to.put("html",html);
            flow.returns(to);
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreatePayOrderTask());
        flow.add(new HandleOrderNotifyCheckTask());
    }
}
