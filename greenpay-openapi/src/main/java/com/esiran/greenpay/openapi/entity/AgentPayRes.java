package com.esiran.greenpay.openapi.entity;

import lombok.Data;

@Data
public class AgentPayRes {
    private String orderNo;
    private String orderSn;
    private String outOrderNo;
    private String batchNo;
    private Integer mchId;
    private Integer amount;
    private Integer fee;
    private Integer accountType;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankNumber;
    private String notifyUrl;
    private String extra;
    private String payTypeCode;



    private Integer agentpayPassageId;


    private String agentpayPassageName;


    private Integer agentpayPassageAccId;


    private Integer payInterfaceId;


    private String payInterfaceAttr;


    private Integer status;
}
