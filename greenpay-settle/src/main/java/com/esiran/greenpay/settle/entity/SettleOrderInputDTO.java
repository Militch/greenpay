package com.esiran.greenpay.settle.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户结算订单
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class SettleOrderInputDTO {
    @NotNull(message = "商户ID不能为空")
    private Integer mchId;
    @NotNull(message = "金额不能为空")
    @Min(value = 1, message = "金额至少不能低于1")
    private Integer amount;
    @NotBlank(message = "账户名不能为空")
    private String accountName;
    @NotBlank(message = "账号不能为空")
    private String accountNumber;
    @NotBlank(message = "开户行不能为空")
    private String bankName;
    private String bankCode;
    private String bankAddress;

}
