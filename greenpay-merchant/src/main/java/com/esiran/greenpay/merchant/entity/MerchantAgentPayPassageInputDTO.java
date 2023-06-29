package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户代付通道
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Data
public class MerchantAgentPayPassageInputDTO {

    @NotNull(message = "商户ID不能为空")
    private Integer merchantId;

    @NotNull(message = "通道ID不能为空")
    private Integer passageId;

    @NotBlank(message = "通道名称不能为空")
    private String passageName;

    @NotNull(message = "收费类型不能为空")
    private Integer feeType;
    @NotNull(message = "商户费率不能为空")
    private BigDecimal feeRate;
    @NotNull(message = "固定费率不能为空")
    private Integer feeAmount;
    @NotNull(message = "轮询权重不能为空")
    private Integer weight;

    @NotNull(message = "状态不能为空")
    private Boolean status;
}
