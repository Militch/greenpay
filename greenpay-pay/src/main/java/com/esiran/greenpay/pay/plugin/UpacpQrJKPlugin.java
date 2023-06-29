package com.esiran.greenpay.pay.plugin;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.RSASignType;
import com.esiran.greenpay.common.sign.SignType;
import com.esiran.greenpay.common.sign.SignVerify;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.OkHttpUtil;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.entity.OrderDetail;
import com.esiran.greenpay.pay.entity.PayOrder;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UpacpQrJKPlugin implements Plugin<PayOrder> {
    private static final OkHttpClient okHttpClient;
    private static final Logger logger = LoggerFactory.getLogger(UpacpQrJKPlugin.class);
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    private static final class CreateOrderTask implements Task<PayOrder> {
        @Autowired
        private PoolingHttpClientConnectionManager pool;
        @Override
        public String taskName() {
            return "createOrderTask";
        }

        @Override
        public String dependent() {
            return "create";
        }
        public String orderTime(){
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return dtf.format(time);
        }

        public OkHttpClient post(Map<String ,String> paramsMap, String url) throws IOException {
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(pool).build();
            HttpPost httpPost = new HttpPost(url);
            //创建参数List集合,并封装参数
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (String key : paramsMap.keySet()) {
                if (!StringUtils.isEmpty(paramsMap.get(key))){
                    nameValuePairs.add(new BasicNameValuePair(key,paramsMap.get(key)));
                }
            }
            //创建表单的Entity对象,第一个参数是封装号的表单数据,第二个参数是编码
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs, "utf8");
            //设置表单的Entity对象到Post请求中
            httpPost.setEntity(formEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            System.out.println("返回参数："+ response);
            return null;
        }

        @Override
        public void action(Flow<PayOrder> flow) throws Exception {
            logger.info("银联扫码");
            PayOrder payOrder = flow.getData();
            Order order = payOrder.getOrder();
            OrderDetail orderDetail = payOrder.getOrderDetail();
            String attrJson = orderDetail.getPayInterfaceAttr();
            Map<String,String> attrMap = MapUtil.jsonString2stringMap(attrJson);
            if (attrMap == null) throw new APIException("请求参数有误","CHANNEL_REQUEST_ERROR");
            String merchantNo = attrMap.get("merchantNo");
            String apiClientPrivKey = attrMap.get("apiClientPrivKey");
            Map<String,String> map = new HashMap<>();
            map.put("service","bankPay");//接口名称
            map.put("merchantNo",merchantNo);//商户编号
            map.put("bgUrl",payOrder.getNotifyReceiveUrl());//服务器接收支付结果的 后台地址
            map.put("version","V2.0");//网关版本
            map.put("payChannelCode","H5_CX_DC");//支付通道编码
            map.put("payChannelType","1");//支付通道类型
            map.put("productName",order.getSubject());//支付通道编码
            map.put("orderNo",order.getOrderNo());//商户订单号
            map.put("orderAmount",String.valueOf(order.getAmount()));//商户订单金额
            map.put("curCode","CNY");//交易币种
            map.put("orderTime",orderTime());//订单时间
            map.put("orderSource","1");//订单来源
            map.put("signType","2");//签名类型
            String principal = MapUtil.sortAndSerialize(map);
            SignType signType = new RSASignType(principal);
            String sign = signType.sign2(apiClientPrivKey);
            sign = URLEncoder.encode(sign,"UTF-8");
            map.put("sign",sign);
            logger.info("sign: {}",sign);
            logger.info("apiPrivKey: {}",apiClientPrivKey);
            logger.info("Request: {}",MapUtil.sortAndSerialize(map));
            FormBody formBody = OkHttpUtil.map2fromBody(map);
            Request request = new Request.Builder()
                    .url("http://cashier.exilenttextile.com/bankPay")
                    .post(formBody)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                String body = responseBody.string();
                logger.info("Response body: {}", body);
                if (StringUtils.isEmpty(body)) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                Pattern pattern = Pattern.compile("^<form .+$",Pattern.DOTALL);
                Matcher matcher = pattern.matcher(body);
//                body = new String(Base64.getEncoder().encode(body.getBytes(StandardCharsets.UTF_8)));
                if (!matcher.matches()) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("resultUrl",null);
                objectMap.put("resultBody",body);
                flow.returns(objectMap);
            }
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
        }
    }
    @Override
    public void apply(Flow<PayOrder> flow) {
        flow.add(new CreateOrderTask());
    }
}
