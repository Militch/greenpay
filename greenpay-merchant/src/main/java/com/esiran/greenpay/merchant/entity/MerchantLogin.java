package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MerchantLogin {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
