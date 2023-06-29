package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 商户产品通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class MerchantProductPassageInputDTO {
    private Integer mchId;
    private Integer productId;
    private Integer passageId;
    private Integer widget;
    private Boolean usage;
}
