package com.esiran.greenpay.openapi.entity;

import lombok.Data;

@Data
public class AgentPayRes {
    private String orderNo;
    private String orderSn;
    private String outOrderNo;
    private String batchNo;
    private Integer amount;
    private Integer fee;
    private Integer accountType;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankNumber;
    private String extra;
    private Integer status;
    private String createdAt;
    private String updatedAt;
}
