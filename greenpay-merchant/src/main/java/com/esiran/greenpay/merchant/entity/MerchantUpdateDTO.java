package com.esiran.greenpay.merchant.entity;

import com.esiran.greenpay.common.util.PatternUtil;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;


@Data
public class MerchantUpdateDTO {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = PatternUtil.REGEXP_USERNAME, message = "用户名格式校验失败")
    private String username;
    @NotBlank(message = "商户名称不能为空")
    private String name;
    @NotBlank(message = "电子邮箱不能为空")
    @Pattern(regexp = PatternUtil.REGEXP_EMAIL, message = "邮箱格式校验失败")
    private String email;
    private String phone;
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
