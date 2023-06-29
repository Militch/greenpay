package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MerchantSercurityDTO {

    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "确认密码不能为空")
    private String conPassword;
}
