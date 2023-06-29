package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户支付账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class PayAccountDTO {
    private Integer availBalance;

    private String availBalanceDisplay;

    private Integer freezeBalance;

    private String freezeBalanceDisplay;
}
