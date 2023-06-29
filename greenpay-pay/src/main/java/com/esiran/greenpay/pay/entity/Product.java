package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付产品
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_product")
public class Product extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 支付产品名称
     */
    private String productName;
    /**
     * 支付产品编码
     */
    private String productCode;

    /**
     * 支付产品类型（1：收款，2：充值）
     */
    private Integer productType;

    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 支付接口模式（1：单独，2：轮询）
     */
    private Integer interfaceMode;

    /**
     * 默认通道ID
     */
    private Integer defaultPassageId;

    /**
     * 默认通道账户ID
     */
    private Integer defaultPassageAccId;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;

}
