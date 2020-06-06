package com.esiran.greenpay.agentpay.entity;

import com.esiran.greenpay.common.util.NumberUtil;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 代付订单表
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class AgentPayOrderInputVO {

    private static final long serialVersionUID = 1L;
    private Integer mchId;
    /**
     * 订单状态（1：待处理，2：处理中，3：处理成功，4：处理失败）
     */
    private Integer status;

    /**
     * 订单号
     */
    private String orderNo;



    /**
     * 创建时间
     */
    private String startTime;

    /**
     * 更新时间
     */
    private String endTime;
}
