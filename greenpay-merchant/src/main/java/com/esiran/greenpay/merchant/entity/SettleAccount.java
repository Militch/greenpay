package com.esiran.greenpay.merchant.entity;

import java.math.BigDecimal;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户结算账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("merchant_settle_account")
public class SettleAccount extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    private Integer merchantId;

    /**
     * 结算收费类型（1：百分比收费，2：固定收费）
     */
    private Integer settleFeeType;

    /**
     * 结算费率（百分比）
     */
    private BigDecimal settleFeeRate;

    /**
     * 结算费用（单位，分）
     */
    private Integer settleFeeAmount;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;

}
