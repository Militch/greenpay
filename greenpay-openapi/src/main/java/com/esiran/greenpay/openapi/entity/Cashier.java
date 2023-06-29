package com.esiran.greenpay.openapi.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Cashier {
    private String orderNo;
    private String orderSn;
    private String appId;
    private String outOrderNo;
    private Integer amount;
    private Integer fee;
    private String channel;
    private String subject;
    private String body;
    private Map<String,Object> channelExtra;
    private String clientIp;
    private String notifyUrl;
    private String redirectUrl;
    private Map<String,Object> credential;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
