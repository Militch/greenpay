package com.esiran.greenpay.settle.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 结算设置
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class SettleConfigInputDTO {

    @NotNull(message = "状态不能为空")
    private Boolean status;

    @NotNull(message = "审核状态不能为空")
    private Boolean auditStatus;

    @NotNull(message = "结算类型不能为空")
    private Integer settleType;

    @NotNull(message = "单笔金额限制最小值不能为空")
    @Min(value = 0,message = "单笔金额限制，最小值不能小于0")
    private BigDecimal amountLimitMinVal;

    @NotNull(message = "单笔金额限制最大值不能为空")
    @DecimalMin(value = "0", message = "单笔金额限制，最大值不能小于0")
    private BigDecimal amountLimitMaxVal;

    @NotNull(message = "每日金额限制最大值不能为空")
    @DecimalMin(value = "0", message = "每日金额限制，最大值不能小于0")
    private BigDecimal dayAmountLimitMaxVal;

    @NotNull(message = "结算费率类型不能为空")
    private Integer settleFeeTypeVal;

    @NotNull(message = "结算比例不能为空")
    @DecimalMin(value = "0", message = "结算比例，不能小于0")
    private BigDecimal settleRate;

    @NotNull(message = "固定金额不能为空")
    @DecimalMin(value = "0", message = "结算比例，不能小于0")
    private BigDecimal settleFeeVal;

}
