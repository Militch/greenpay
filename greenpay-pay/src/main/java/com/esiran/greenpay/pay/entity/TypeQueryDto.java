package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
public class TypeQueryDto {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 支付类型编码
     */
    private String typeCode;

    /**
     * 支付类型名称
     */
    private String typeName;


    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;
}
