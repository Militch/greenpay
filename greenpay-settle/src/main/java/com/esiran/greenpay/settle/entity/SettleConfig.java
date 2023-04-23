package com.esiran.greenpay.settle.entity;

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
 * 结算设置
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("settle_config")
public class SettleConfig extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;

    /**
     * 审核状态（0：关闭，1：开启）
     */
    private Boolean auditStatus;

    /**
     * 结算类型（1：人工结算，2：自动结算）
     */
    private Integer settleType;

    /**
     * 金额限制（最小值，单位：分）
     */
    private Integer amountLimitMin;

    /**
     * 金额限制（最大值，单位：分）
     */
    private Integer amountLimitMax;

    /**
     * 每日金额最大值（单位：分）
     */
    private Integer dayAmountLimitMax;

    /**
     * 结算费率类型（1：比例收费，2：固定收费，3：比例收费+固定收费）
     */
    private Integer settleFeeType;

    /**
     * 结算比例（保留两位小数）
     */
    private BigDecimal settleRate;

    /**
     * 固定费率（单位：分）
     */
    private Integer settleFee;


}
