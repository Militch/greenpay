package com.esiran.greenpay.agentpay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 代付通道账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("agentpay_passage_account")
public class AgentPayPassageAccount extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 代付通道ID
     */
    private Integer passageId;

    /**
     * 通道账户名称
     */
    private String accountName;

    /**
     * 通道接口参数
     */
    private String interfaceAttr;
    /**
     * 轮询权重
     */
    private Integer weight;
    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;


}
