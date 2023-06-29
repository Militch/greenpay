package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单详情
 * </p>
 *
 * @author Militch
 * @since 2020-04-14
 */
@Data
public class OrderDetailDTO {
    private String orderNo;

    private String payTypeCode;

    /**
     * 支付产品ID
     */
    private Integer payProductId;

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
     * 支付接口请求参数
     */
    private String payInterfaceAttr;

    /**
     * 上游订单编号
     */
    private String upstreamOrderNo;

    private String payCredential;

    private String upstreamExtra;

}
