package com.esiran.greenpay.openapi.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Invoice {
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 交易流水号
     */
    private String orderSn;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 商户订单号
     */
    private String outOrderNo;
    /**
     * 订单金额（单位：分）
     */
    private Integer amount;
    /**
     * 订单手续费（单位：分）
     */
    private Integer fee;
    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 商品标题
     */
    private String subject;
    /**
     * 商品描述信息
     */
    private String body;
    /**
     * 渠道额外参数
     */
    private Map<String,Object> channelExtra;
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
    /**
     * 支付凭证
     */
    private Map<String,Object> credential;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 支付时间
     */
    private LocalDateTime paidAt;
}
