package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class MerMerchantUpdateDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "商户名称不能为空")
    private String name;
    @NotBlank(message = "电子邮箱不能为空")
    private String email;
    @NotBlank(message = "联系手机不能为空")
    private String phone;
}
