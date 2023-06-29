package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Integer id;
    private String productCode;
    private String productName;
    private Integer productType;
    private String payTypeCode;
    private String payTypeName;
    private Integer interfaceMode;
    private Integer defaultPassageId;
    private Integer defaultPassageAccId;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
