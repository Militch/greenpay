package com.esiran.greenpay.pay.entity;

import lombok.Data;


/**
 * <p>
 * 支付订单
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
@Data
public class ExtractQueryDTO {
    private String orderNo;
    private String orderSn;
    private String accountName;
    private Integer status;
    private String startTime;
    private String endTime;
}
