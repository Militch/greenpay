package com.esiran.greenpay.merchant.entity;

import lombok.Data;

/**
 * <p>
 * 商户预充值账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class PrepaidAccountDTO {
    private Integer availBalance;
    private Integer freezeBalance;
    private String availBalanceDisplay;
    private String freezeBalanceDisplay;
}
