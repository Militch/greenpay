package com.esiran.greenpay.pay.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付通道账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class PassageAccountInputDTO {
    @NotNull(message = "通道ID不能为空")
    private Integer passageId;

    @NotBlank(message = "账户名称不能为空")
    private String accountName;
    private String interfaceAttr;
    private Integer weight;
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
