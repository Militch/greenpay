package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UsernamePasswordInputDTO {
    @NotBlank(message = "请输入用户名")
    private String username;
    @NotBlank(message = "请输入密码")
    private String password;
}
