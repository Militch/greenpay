package com.esiran.greenpay.agentpay.plugin;

import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.agentpay.entity.AgentPayOrder;

public class AgentPayOrderFlow extends Flow<AgentPayOrder> {
    public AgentPayOrderFlow(AgentPayOrder data) {
        super(data);
    }
}
