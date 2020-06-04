package com.esiran.greenpay.openapi.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TransferInputDTO {
    @NotNull(message = "订单号不能为空")
    private String outOrderNo;
    @NotNull(message = "订单金额不能为空")
    private Integer amount;
    @NotNull(message = "账户类型不能为空")
    private Integer accountType;
    @NotBlank(message = "账户名不能为空")
    private String accountName;
    @NotBlank(message = "账户号不能为空")
    private String accountNumber;
    @NotBlank(message = "开户行不能为空")
    private String bankName;
    private String bankNumber;
    private String notifyUrl;
    private String extra;
}
