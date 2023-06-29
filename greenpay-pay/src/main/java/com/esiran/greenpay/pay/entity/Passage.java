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
 * 支付通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_passage")
public class Passage  extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 支付通道名称
     */
    private String passageName;

    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 支付接口编码
     */
    private String interfaceCode;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;


}
