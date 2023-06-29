package com.esiran.greenpay.settle.entity;

import lombok.Data;

/**
 * @author han
 * @Package com.esiran.greenpay.settle.entity
 * @date 2020/5/12 16:52
 */
@Data
public class SettleOrderQueryDto {
    private static final long serialVersionUID = 1L;
    /**
     * 结算订单号
     */
    private String orderNo;

    /**
     * 结算流水号
     */
    private String orderSn;

    /**
     * 商户ID
     */
    private Integer mchId;

    private String subject;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     *开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
