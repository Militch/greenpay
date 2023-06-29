package com.esiran.greenpay.agentpay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 代付批次
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("agentpay_batch")
public class AgentPayBatch extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交易批次号
     */
    private String batchNo;

    /**
     * 商户交易批次号
     */
    private String outBatchNo;

    /**
     * 商户ID
     */
    private Integer mchId;

    /**
     * 总金额（单位：分）
     */
    private Integer totalAmount;

    /**
     * 总笔数
     */
    private Integer totalCount;

    /**
     * 订单回调地址
     */
    private String notifyUrl;

    /**
     * 扩展参数
     */
    private String extra;

    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 代付通道ID
     */
    private Integer agentpayPassageId;

    /**
     * 代付通道账户ID
     */
    private Integer agentpayPassageAccId;

    /**
     * 支付接口ID
     */
    private Integer payInterfaceId;

    /**
     * 订单状态（1：待处理，2：处理中，3：处理成功，4：部分成功，-1：处理失败）
     */
    private Integer status;


}
