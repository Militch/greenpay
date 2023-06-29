package com.esiran.greenpay.agentpay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 代付通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("agentpay_passage")
public class AgentPayPassage extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 代付通道名称
     */
    private String passageName;

    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 支付接口编码
     */
    private String interfaceCode;
    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;


}
