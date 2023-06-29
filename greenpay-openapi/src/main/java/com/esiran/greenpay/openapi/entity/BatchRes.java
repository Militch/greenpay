package com.esiran.greenpay.openapi.entity;

import lombok.Data;

@Data
public class BatchRes {
    private String batchNo;
    private String outBatchNo;
    private Integer totalAmount;
    private Integer totalCount;
    private Integer status;
    private String extra;
    private String createdAt;
    private String updatedAt;
}
