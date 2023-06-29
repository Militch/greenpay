package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付产品通道子账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class ProductPassageInputDTO {
    private Integer productId;
    private Integer passageId;
    private Integer widget;
    private Boolean usage;
}
