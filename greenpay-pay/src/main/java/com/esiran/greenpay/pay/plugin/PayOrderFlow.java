package com.esiran.greenpay.pay.plugin;

import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.pay.entity.PayOrder;

public class PayOrderFlow extends Flow<PayOrder> {
    public PayOrderFlow(PayOrder data) {
        super(data);
    }
}
