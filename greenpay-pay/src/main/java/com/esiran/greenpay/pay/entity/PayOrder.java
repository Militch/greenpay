package com.esiran.greenpay.pay.entity;

import lombok.Data;

@Data
public class PayOrder {

    private String notifyReceiveUrl;
    private Order order;
    private OrderDetail orderDetail;
}
