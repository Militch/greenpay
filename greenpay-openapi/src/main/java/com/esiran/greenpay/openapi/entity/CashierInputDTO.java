package com.esiran.greenpay.openapi.entity;

import com.esiran.greenpay.common.entity.BaseSignInput;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CashierInputDTO extends BaseSignInput {
    /**
     * 应用ID
     */
    @NotBlank(message = "应用ID不能为空")
    private String appId;
    /**
     * 商户订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    private String outOrderNo;
    /**
     * 订单金额（单位：分），必须大于0
     */
    @Min(value = 1,message = "订单金额（单位：分）必须大于0")
    @NotNull(message = "订单金额不能为空")
    private Integer amount;
    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    private String subject;
    @NotBlank(message = "支付渠道不能为空")
    private String channel;
    /**
     * 商品描述信息
     */
    private String body;
    /**
     * 客户端地址
     */
    private String clientIp;
    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 回退地址
     */
    private String redirectUrl;
}
