package com.esiran.greenpay.settle.entity;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * <p>
 * 结算设置
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class SettleConfigDTO {

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
    private String amountLimitMinDisplay;

    /**
     * 金额限制（最大值，单位：分）
     */
    private Integer amountLimitMax;
    private String amountLimitMaxDisplay;

    /**
     * 每日金额最大值（单位：分）
     */
    private Integer dayAmountLimitMax;
    private String dayAmountLimitMaxDisplay;

    /**
     * 结算费率类型（1：比例收费，2：固定收费，3：比例收费+固定收费）
     */
    private Integer settleFeeType;

    /**
     * 结算比例（保留两位小数）
     */
    private BigDecimal settleRate;
    private String settleRateDisplay;

    /**
     * 固定费率（单位：分）
     */
    private Integer settleFee;
    private String settleFeeDisplay;

}
