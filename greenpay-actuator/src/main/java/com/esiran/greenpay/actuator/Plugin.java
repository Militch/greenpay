package com.esiran.greenpay.actuator;

import com.esiran.greenpay.actuator.entity.Flow;

public interface Plugin<T> {
    void apply(Flow<T> flow);
}
