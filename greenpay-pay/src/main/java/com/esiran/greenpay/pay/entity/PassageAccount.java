package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付通道账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_passage_account")
public class PassageAccount  extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;
    private String payTypeCode;
    /**
     * 支付通道ID
     */
    private Integer passageId;

    /**
     * 通道账户名称
     */
    private String accountName;

    /**
     * 通道接口参数
     */
    private String interfaceAttr;
    /**
     * 轮训权重
     */
    private Integer weight;
    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;


}
