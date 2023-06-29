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
 * 代付订单表
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class AgentPayOrderDTO {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单流水号
     */
    private String orderSn;
    private String outOrderNo;

    /**
     * 代付批次号
     */
    private String batchNo;

    /**
     * 商户ID
     */
    private Integer mchId;

    /**
     * 订单金额（单位：分）
     */
    private Integer amount;

    /**
     * 订单手续费（单位：分）
     */
    private Integer fee;
    private String feeDisplay;
    private String amountDisplay;
    /**
     * 账户类型（1：对公，2：对私）
     */
    private Integer accountType;
    /**
     * 账户名
     */
    private String accountName;

    /**
     * 账户号
     */
    private String accountNumber;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 联行号
     */
    private String bankNumber;

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
     * 支付通道ID
     */
    private Integer agentpayPassageId;

    /**
     * 支付通道ID
     */
    private Integer agentpayPassageAccId;

    /**
     * 代付通道名称
     */
    private String agentpayPassageName;

    /**
     * 支付接口ID
     */
    private Integer payInterfaceId;

    /**
     * 支付接口参数
     */
    private String payInterfaceAttr;

    /**
     * 订单状态（1：待处理，2：处理中，3：处理成功，4：处理失败）
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

    private LocalDateTime expiredAt;
    private String expiredAtDisplay;

    private String startTime;
    private String endTime;

    public static AgentPayOrderDTO convertOrderEntity(AgentPayOrder agentPayOrder){
        if (agentPayOrder == null){ return null;}
        AgentPayOrderDTO dto = modelMapper.map(agentPayOrder, AgentPayOrderDTO.class);
        dto.setAmountDisplay("￥".concat(NumberUtil.amountFen2Yuan(agentPayOrder.getAmount())));
        dto.setFeeDisplay("￥".concat(NumberUtil.amountFen2Yuan(agentPayOrder.getFee())));
        String status = agentPayOrder.getStatus() == 1 ? "待处理"
                : agentPayOrder.getStatus() == 2 ? "处理中"
                : agentPayOrder.getStatus() == 3 ? "处理成功"
                : agentPayOrder.getStatus() == -1 ? "处理失败"
                : "未知";
        dto.setStatusDisplay(status);
        dto.setCreatedAtDisplay(dtf.format(agentPayOrder.getCreatedAt()));
        dto.setUpdatedAtDisplay(dtf.format(agentPayOrder.getUpdatedAt()));
        return dto;
    }
}
