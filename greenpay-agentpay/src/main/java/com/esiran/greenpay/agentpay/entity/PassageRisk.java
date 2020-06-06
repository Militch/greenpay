package com.esiran.greenpay.agentpay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Militch
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("agentpay_passage_risk")
public class PassageRisk extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 代付通道id
     */
    private Integer passageId;

    /**
     * 代付通道名称
     */
    private String passageName;

    /**
     * 单笔代付最低金额
     */
    private Integer amountMin;

    /**
     * 单笔代付最大金额
     */
    private Integer amountMax;

    /**
     * 0 关闭 1 开启
     */
    private Integer status;


}
