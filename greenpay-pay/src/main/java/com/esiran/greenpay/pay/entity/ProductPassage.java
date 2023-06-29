package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付产品通道子账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_product_passage")
public class ProductPassage extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 支付通道ID
     */
    private Integer passageId;

    private Integer widget;

}
