package com.esiran.greenpay.openapi.entity;

import com.esiran.greenpay.common.entity.BaseSignInput;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class WxOPenIdInputDTO  extends BaseSignInput {
    @NotBlank(message = "重定向地址不能为空")
    private String redirectUrl;
}
