package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户预充值账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("merchant_prepaid_account")
public class PrepaidAccount extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    private Integer merchantId;

    /**
     * 可用余额（分）
     */
    private Integer availBalance;

    /**
     * 冻结金额（分）
     */
    private Integer freezeBalance;
}
