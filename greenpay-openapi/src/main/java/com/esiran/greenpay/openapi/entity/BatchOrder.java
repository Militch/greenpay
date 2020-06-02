package com.esiran.greenpay.openapi.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BatchOrder {

    private String batchNo;

    private String outOrderNo;

    private Integer amount;

    private Integer accountType;

    private String accountName;

    private String accountNumber;

    private String bankName;

    private String bankNumber;

    private String notifyUrl;


}
