package com.esiran.greenpay.merchant.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户产品通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("merchant_product_passage")
public class MerchantProductPassage extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;
    private Integer mchId;
    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 支付通道ID
     */
    private Integer passageId;

    /**
     * 权重
     */
    private Integer widget;


}
