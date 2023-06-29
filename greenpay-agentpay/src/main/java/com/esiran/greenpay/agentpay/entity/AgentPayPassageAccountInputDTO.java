package com.esiran.greenpay.agentpay.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 支付通道账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class AgentPayPassageAccountInputDTO {
    @NotNull(message = "通道ID不能为空")
    private Integer passageId;

    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    private String interfaceAttr;

    @NotNull(message = "轮询权重不能为空")
    private Integer weight;
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
