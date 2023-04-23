package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.esiran.greenpay.common.util.NumberUtil;
import com.esiran.greenpay.pay.service.impl.OrderServiceImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 支付订单
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
@Data
public class OrderDTO  {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 交易流水号
     */
    private String orderSn;

    /**
     * 商户ID
     */
    private Integer mchId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商品标题
     */
    private String subject;

    /**
     * 商户订单号
     */
    private String outOrderNo;

    /**
     * 订单金额（单位：分）
     */
    private Integer amount;
    private Integer fee;
    private String feeDisplay;
    private String amountDisplay;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 客户端IP
     */
    private Integer clientIp;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 跳转地址
     */
    private String redirectUrl;

    /**
     * 订单状态（0：待付款，2：已支付，3：订单完成，-1：交易取消，-2：交易失败）
     */
    private Integer status;
    private String statusDisplay;

    /**
     * 支付时间
     */
    private LocalDateTime paidAt;
    private String paidAtDisplay;


    /**
     * 支付产品ID
     */
    private Integer payProductId;
    private String payProductName;
    private String payProductCode;
    /**
     * 支付通道ID
     */
    private Integer payPassageId;

    /**
     * 支付通道子账户ID
     */
    private Integer payPassageAccId;

    /**
     * 支付接口ID
     */
    private Integer payInterfaceId;

    /**
     * 支付接口参数
     */
    private String payInterfaceAttr;

    /**
     * 上游订单编号
     */
    private String upstreamOrderNo;

    /**
     * 上游扩展参数
     */
    private String upstreamExtra;

    private String payTypeName;

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

    public static  OrderDTO convertOrderEntity(Order order){
        if (order == null) return null;
        OrderDTO dto = modelMapper.map(order,OrderDTO.class);
        dto.setAmountDisplay("￥".concat(NumberUtil.amountFen2Yuan(order.getAmount())));
        dto.setFeeDisplay(NumberUtil.amountFen2Yuan(order.getFee()));
        String status = order.getStatus() == 1 ? "待支付"
                : order.getStatus() == 2 ? "已支付"
                : order.getStatus() == 3 ? "已完成"
                : order.getStatus() == -1 ? "交易取消"
                : order.getStatus() == -2 ? "交易失败"
                : "未知";
        dto.setStatusDisplay(status);
        dto.setCreatedAtDisplay(dtf.format(order.getCreatedAt()));
        dto.setUpdatedAtDisplay(dtf.format(order.getUpdatedAt()));
        if (order.getPaidAt()!=null)
            dto.setPaidAtDisplay(dtf.format(order.getPaidAt()));
        if (order.getExpiredAt() != null)
            dto.setExpiredAtDisplay(dtf.format(order.getExpiredAt()));
        return dto;
    }

}
