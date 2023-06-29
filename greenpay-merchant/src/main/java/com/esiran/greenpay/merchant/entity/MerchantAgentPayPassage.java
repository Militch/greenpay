package com.esiran.greenpay.merchant.entity;

import java.math.BigDecimal;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户代付通道
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("merchant_agentpay_passage")
public class MerchantAgentPayPassage extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    private Integer merchantId;

    /**
     * 通道ID
     */
    private Integer passageId;

    /**
     * 通道名称
     */
    private String passageName;


    /**
     * 手续费类型（1：百分比收费，2：固定收费，3：百分比加固定收费）
     */
    private Integer feeType;

    /**
     * 通道费率
     */
    private BigDecimal feeRate;

    /**
     * 固定费用（单位：分）
     */
    private Integer feeAmount;

    /**
     * 轮询权重
     */
    private Integer weight;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;


}
