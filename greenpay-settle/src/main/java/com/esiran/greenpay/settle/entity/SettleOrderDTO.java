package com.esiran.greenpay.settle.entity;

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
 * 商户结算订单
 * </p>
 *
 * @author Militch
 * @since 2020-04-27
 */
@Data
public class SettleOrderDTO {


    private static final long serialVersionUID = 1L;

    private Integer id;



    /**
     * 结算订单号
     */
    private String orderNo;

    /**
     * 结算流水号
     */
    private String orderSn;

    /**
     * 商户ID
     */
    private Integer mchId;

    /**
     * 结算类型（1：人工结算，2：自动结算）
     */
    private Integer settleType;

    /**
     * 订单金额（单位：分）
     */
    private Integer amount;
    private String amountDisplay;

    /**
     * 结算手续费（单位：分）
     */
    private Integer fee;
    private String feeDisplay;

    /**
     * 结算金额（单位：分）
     */
    private Integer settleAmount;

    private String settleAmountDisplay;
    /**
     * 结算账户名
     */
    private String accountName;

    /**
     * 结算账号
     */
    private String accountNumber;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 支付类型编码
     */
    private String payTypeCode;


    /**
     * 支付通道ID
     */
    private Integer agentpayPassageId;

    /**
     * 支付通道账户ID
     */
    private Integer agentpayPassageAccId;

    /**
     * 支付接口ID
     */
    private Integer payInterfaceId;
    /**
     * 支付接口参数
     */
    private String payInterfaceAttr;

    /**
     * 状态（1：待审核，2：处理中，3：已结算，-1：已驳回，-2：结算失败）
     */
    private Integer status;

    private String statusDisplay;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    private LocalDateTime settledAt;
    private String settledAtDisplay;
    private String createdAtDisplay;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    private String updatedAtDisplay;

}
