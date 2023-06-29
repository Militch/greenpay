package com.esiran.greenpay.agentpay.plugin;

import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;
import com.esiran.greenpay.agentpay.entity.AgentPayPassageAccount;

public class AgentPayQueryFlow extends Flow<AgentPayPassageAccount> {
    public AgentPayQueryFlow(AgentPayPassageAccount data) {
        super(data);
    }
}
