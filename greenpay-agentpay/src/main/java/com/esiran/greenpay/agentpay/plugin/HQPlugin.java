package com.esiran.greenpay.agentpay.plugin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.service.IAgentPayOrderService;
import com.esiran.greenpay.bank.pingan.api.PingAnApiEx;
import com.esiran.greenpay.bank.pingan.entity.HeaderMsg;
import com.esiran.greenpay.bank.pingan.entity.OnceAgentPay;
import com.esiran.greenpay.bank.pingan.entity.QueryOnceAgentPay;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.sign.Md5SignType;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.pay.entity.Order;
import com.esiran.greenpay.pay.plugin.HQPayPlugin;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class HQPlugin implements Plugin<AgentPayOrder> {
    private static final Gson g = new Gson();
    private static final OkHttpClient okHttpClient;
    private static final Logger logger = LoggerFactory.getLogger(HQPlugin.class);
    private static IAgentPayOrderService agentPayOrderService;
    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }
    private HQPlugin(IAgentPayOrderService agentPayOrderService){
        this.agentPayOrderService = agentPayOrderService;
    }
    private static final class OrderCreateTask implements Task<AgentPayOrder> {

        @Override
        public String taskName() {
            return "PinAnCreate";
        }

        @Override
        public String dependent() {
            return "create";
        }

        @Override
        public void action(Flow<AgentPayOrder> flow) throws Exception {
            AgentPayOrder data = flow.getData();
            if (data.getStatus() != 1) {
                throw new APIException("代付订单状态异常", "");
            }
            String attr = data.getPayInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(attr);
            if (attrmap == null
                    || attrmap.get("companyCode") == null
                    || attrmap.get("apiClientPrivKey") == null
                    ){
                throw new APIException("代付接口参数异常","");
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("mchid","");
            map.put("money","");
            map.put("bankname","");
            map.put("subbranch","");
            map.put("accountname","");
            map.put("cardnumber","");
            map.put("province","");
            map.put("city","");
            String principal = MapUtil.sortAndSerialize(map);
            String concat = principal.concat("&key=" + attrmap.get("apiClientPrivKey"));
            Md5SignType signType = new Md5SignType(concat);
            String sign = signType.sign2(null);
            map.put("sign",sign);
            logger.info("sign: {}", sign);
            logger.info("Request: {}", MapUtil.sortAndSerialize(map));
            String json = g.toJson(map);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request request = new Request.Builder()
                    .url("http://pay.nbz8888.com/Payment_Dfpay_add.html")
                    .post(requestBody)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                String body = responseBody.string();
                logger.info("Response body: {}", body);
                Map<String, Object> objectMap = MapUtil.jsonString2objMap(body);
                Map<String,Object> returns = new HashMap<>();
                if (objectMap == null) throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
                if (objectMap.get("status").equals("success")){
                    returns.put("status","success");
                    flow.returns(returns);
                }else {
                    throw new APIException("代付渠道请求失败","");
                }
            }
        }
    }



    @Override
    public void apply(Flow<AgentPayOrder> flow) {
        flow.add(new OrderCreateTask());
    }
}
