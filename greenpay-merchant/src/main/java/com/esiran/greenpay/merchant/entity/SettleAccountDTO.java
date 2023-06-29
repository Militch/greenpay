package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
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
public class SettleAccountDTO {

    private Integer settleFeeType;

    private BigDecimal settleFeeRate;

    private Integer settleFeeAmount;

    private String settleFeeRateDisplay;

    private String settleFeeAmountDisplay;

    private Boolean status;

}
