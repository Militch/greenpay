package com.esiran.greenpay.openapi.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class InvoiceQueryInputDTO {
    @NotBlank(message = "应用ID不能为空")
    private String appId;
    private String orderNo;
    private String outOrderNo;
}
