package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.time.LocalDateTime;


/**
 * <p>
 * 支付订单
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
@Data
public class MchOrderQueryDTO {
    private String orderNo;
    private String orderSn;
    private String outOrderNo;
    private Integer status;
    private String startTime;
    private String endTime;
}
