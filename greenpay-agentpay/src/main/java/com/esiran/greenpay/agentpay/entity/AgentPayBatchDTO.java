package com.esiran.greenpay.agentpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.esiran.greenpay.common.util.NumberUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 代付批次
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Data
public class AgentPayBatchDTO{
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
    private String totalAmountDisplay;
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
    private String statusDisplay;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    private String createdAtDisplay;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    private String updatedAtDisplay;

    public static AgentPayBatchDTO convertOrderEntity(AgentPayBatch agentPayBatch){
        if (agentPayBatch == null ) return null;
        AgentPayBatchDTO dto = modelMapper.map(agentPayBatch, AgentPayBatchDTO.class);
        dto.setTotalAmountDisplay("￥".concat(NumberUtil.amountFen2Yuan(agentPayBatch.getTotalAmount())));
        String status = agentPayBatch.getStatus() == 1 ? "待处理"
                : agentPayBatch.getStatus() == 2 ? "处理中"
                : agentPayBatch.getStatus() == 3 ? "处理成功"
                : agentPayBatch.getStatus() == 4 ? "部分成功"
                : agentPayBatch.getStatus() == -1 ? "处理失败"
                : "未知";
        dto.setStatusDisplay(status);
        dto.setCreatedAtDisplay(dtf.format(agentPayBatch.getCreatedAt()));
        dto.setUpdatedAtDisplay(dtf.format(agentPayBatch.getUpdatedAt()));
        return dto;
    }

}
