package com.esiran.greenpay.merchant.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户产品
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("merchant_product")
public class MerchantProduct extends BaseMapperEntity {
    /**
     * 商户ID
     */
    private Integer merchantId;
    private String payTypeCode;
    /**
     * 产品ID
     */
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer productType;
    /**
     * 接口模式
     */
    private String interfaceMode;
    /**
     * 通道费率
     */
    private BigDecimal rate;

    private Integer defaultPassageId;
    private Integer defaultPassageAccId;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;


}
