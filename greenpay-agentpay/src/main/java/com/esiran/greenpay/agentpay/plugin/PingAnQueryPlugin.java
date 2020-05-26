package com.esiran.greenpay.agentpay.plugin;

import com.esiran.greenpay.actuator.Plugin;
import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

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
            Map<String,Object> out = new HashMap<>();
            out.put("balance", 0);
            flow.returns(out);
        }
    }



    @Override
    public void apply(Flow<AgentPayPassageAccount> flow) {
        flow.add(new AccountQueryBalanceTask());
    }
}
