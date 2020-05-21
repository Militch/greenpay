package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InterfaceDTO {
    private Integer id;
    private String interfaceCode;
    private String interfaceName;
    private String payTypeCode;
    private Boolean status;
    private Integer scenarios;
    private Integer interfaceType;
    private String interfaceImpl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
