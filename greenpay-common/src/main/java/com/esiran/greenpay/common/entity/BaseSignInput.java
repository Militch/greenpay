package com.esiran.greenpay.common.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class BaseSignInput {
    private Long timestamp;
    private String signType;
    private String sign;
    private String apiKey;
}
