package com.esiran.greenpay.openapi.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BatchInputDTO {

    /**
     * 批次号
     */
    @NotEmpty(message = "批次号不能为空")
    private String outBatchNo;
    /**
     * 总金额（单位：分）
     */
    @NotNull(message = "总金额不能为空")
    private Integer totalAmount;

    /**
     * 总笔数
     */
    @NotNull(message = "总笔数不能为空")
    private Integer totalCount;

    /**
     * 扩展参数
     */
    @NotEmpty(message = "扩展参数不能为空")
    private String recipients;

    /**
     * 订单回调地址
     */
    private String notifyUrl;


}
