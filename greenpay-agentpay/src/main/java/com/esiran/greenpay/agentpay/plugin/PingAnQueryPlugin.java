package com.esiran.greenpay.agentpay.plugin;

import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.esiran.greenpay.bank.pingan.api.PingAnApiEx;
import com.esiran.greenpay.bank.pingan.entity.HeaderMsg;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.NumberUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class PingAnQueryPlugin implements Plugin<AgentPayPassageAccount> {
    private static final Gson g = new Gson();
    private static final class AccountQueryBalanceTask implements Task<AgentPayPassageAccount> {

        @Override
        public String taskName() {
            return "queryPingAnBalance";
        }

        @Override
        public String dependent() {
            return "queryBalance";
        }

        @Override
        public void action(Flow<AgentPayPassageAccount> flow) throws Exception {
            AgentPayPassageAccount data = flow.getData();
            String interfaceAttr = data.getInterfaceAttr();
            Map<String, String> attrmap = MapUtil.jsonString2stringMap(interfaceAttr);
            if (attrmap == null
                    || attrmap.get("companyCode") == null
                    || attrmap.get("acctNo") == null
                    || attrmap.get("host") == null){
                throw new APIException("代付接口参数异常","");
            }
            HeaderMsg headerMsg = new HeaderMsg();
            String number = EncryptUtil.baseTimelineCode();
            headerMsg.setOutOrderNumber(number);
            headerMsg.setCompanyCode(attrmap.get("companyCode"));
            PingAnApiEx apiEx = new PingAnApiEx(attrmap.get("host"), headerMsg);
            Map<String, String> queryAmount = apiEx.queryAmount(attrmap.get("acctNo"));
            if (queryAmount == null){
                throw new APIException("代付渠道请求失败","");
            }
            String balance = queryAmount.get("Balance");
            Integer blanceFen = NumberUtil.amountYuan2fen(new BigDecimal(balance));
            Map<String,Object> out = new HashMap<>();
            out.put("balance", blanceFen);
            flow.returns(out);
        }
    }



    @Override
    public void apply(Flow<AgentPayPassageAccount> flow) {
        flow.add(new AccountQueryBalanceTask());
    }
}
