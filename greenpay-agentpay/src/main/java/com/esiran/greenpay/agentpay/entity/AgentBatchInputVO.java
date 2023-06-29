package com.esiran.greenpay.agentpay.entity;

import lombok.Data;

/**
 * <p>
 * 代付订单表
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class AgentBatchInputVO {

    private static final long serialVersionUID = 1L;
    private Integer mchId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 交易批次号
     */
    private String batchNo;

    /**
     * 商户交易批次号
     */
    private String outBatchNo;

    /**
     * 订单状态（1：待处理，2：处理中，3：处理成功，4：处理失败）
     */
    private Integer status;





    /**
     * 创建时间
     */
    private String startTime;

    /**
     * 更新时间
     */
    private String endTime;
}
