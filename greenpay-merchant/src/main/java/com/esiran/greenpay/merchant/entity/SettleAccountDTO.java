package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 商户结算账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-21
 */
@Data
@ApiModel(value = "SettleAccount",description = "结算账户")
public class SettleAccountDTO {

    @ApiModelProperty("结算收费类型（1：百分比收费，2：固定收费）")
    private Integer settleFeeType;

    @ApiModelProperty("结算费率（百分比）")
    private BigDecimal settleFeeRate;

    @ApiModelProperty("结算费用（单位，分）")
    private Integer settleFeeAmount;

    @ApiModelProperty("结算费率（百分比）")
    private String settleFeeRateDisplay;

    @ApiModelProperty("结算费用")
    private String settleFeeAmountDisplay;

    @ApiModelProperty("状态（0：关闭，1：开启）")
    private Boolean status;

}
