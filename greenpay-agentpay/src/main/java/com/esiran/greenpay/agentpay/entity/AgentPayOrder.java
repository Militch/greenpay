package com.esiran.greenpay.agentpay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.modelmapper.ModelMapper;

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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("agentpay_order")
public class AgentPayOrder extends BaseMapperEntity {

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
     * 代付通道名称
     */
    private String agentpayPassageName;

    /**
     * 支付通道ID
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
     * 订单状态（1：待处理，2：处理中，3：处理成功，4：处理失败）
     */
    private Integer status;


}
