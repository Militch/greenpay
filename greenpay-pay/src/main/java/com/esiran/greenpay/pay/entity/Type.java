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
 * 支付类型
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_type")
public class Type  extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 支付类型编码
     */
    private String typeCode;

    /**
     * 支付类型名称
     */
    private String typeName;

    /**
     * 类别(1：支付，2，代付)
     */
    private Integer type;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;
}
