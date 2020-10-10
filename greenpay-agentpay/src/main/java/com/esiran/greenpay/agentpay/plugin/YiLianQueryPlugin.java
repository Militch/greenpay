package com.esiran.greenpay.agentpay.plugin;

import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.MsgBean;
import com.esiran.greenpay.agentpay.entity.MsgBody;
import com.esiran.greenpay.agentpay.util.Util;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class YiLianQueryPlugin implements Plugin<AgentPayOrder> {
    private static final Gson g = new Gson();
    private static final OkHttpClient okHttpClient;
    private static final Logger logger = LoggerFactory.getLogger(YiLianQueryPlugin.class);

    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Duration.ofSeconds(180))
                .writeTimeout(Duration.ofSeconds(180))
                .connectTimeout(Duration.ofSeconds(180))
                .callTimeout(Duration.ofSeconds(180))
                .build();
    }

    private static final class OrderCreateTask implements Task<AgentPayOrder> {

        @Override
        public String taskName() {
            return "yilianQueryBalance";
        }

        @Override
        public String dependent() {
            return "create";
        }

        @Override
        public void action(Flow<AgentPayOrder> flow) throws Exception {
            AgentPayOrder data = flow.getData();
            String attr = data.getPayInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(attr);
            if (attrmap == null
                    || attrmap.get("dna_pub_key") == null
                    || attrmap.get("mer_pfx_key") == null
                    || attrmap.get("user_name") == null
                    || attrmap.get("mer_pfx_pass") == null
                    || attrmap.get("url") == null
            ) {
                throw new APIException("代付接口参数异常", "");
            }
            MsgBean req_bean = new MsgBean();
            req_bean.setVERSION("2.1");
            req_bean.setMSG_TYPE("600001");
            req_bean.setBATCH_NO(data.getBatchNo());//每笔订单不可重复，建议：公司简称缩写+yymmdd+流水号
            req_bean.setUSER_NAME(attrmap.get("user_name"));//系统后台登录名

            String res = Util.sendAndRead(Util.signANDencrypt(req_bean,
                    attrmap.get("mer_pfx_key")
                    ,attrmap.get("mer_pfx_pass")
                    ,attrmap.get("dna_pub_key"))
                    ,attrmap.get("url"));
            if (res == null){
                throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
            }
            MsgBean res_bean = Util.decryptANDverify(res
                    ,attrmap.get("mer_pfx_key")
                    ,attrmap.get("mer_pfx_pass")
                    ,attrmap.get("dna_pub_key"));

            if ("0000".equals(res_bean.getTRANS_STATE())) {
                MsgBody msgBody = res_bean.getBODYS().get(0);
                String amount = msgBody.getAMOUNT();
                Integer amountYuan2fen = NumberUtil.amountYuan2fen(new BigDecimal(amount));
                Map<String,Object> returns = new HashMap<>();
                returns.put("balance",amountYuan2fen);
                flow.returns(returns);
                logger.info("易联代付请求成功");
            }else {
                throw new APIException("支付渠道请求失败","CHANNEL_REQUEST_ERROR");
            }
        }
    }


    @Override
    public void apply(Flow<AgentPayOrder> flow) {
        flow.add(new OrderCreateTask());
    }
}
