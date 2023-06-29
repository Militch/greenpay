package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户代付通道
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Data
public class MerchantAgentPayPassageDTO {

    private Integer id;

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
    private String feeRateDisplay;

    /**
     * 固定费用（单位：分）
     */
    private Integer feeAmount;
    private String feeAmountDisplay;

    /**
     * 轮询权重
     */
    private Integer weight;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
