package com.esiran.greenpay.actuator.entity;

public interface Task<T> {
    String taskName();
    String dependent();
    void action(Flow<T> flow) throws Exception;
}
