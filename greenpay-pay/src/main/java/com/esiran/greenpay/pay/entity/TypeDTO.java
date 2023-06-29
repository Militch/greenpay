package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付类型
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class TypeDTO {

    private static final long serialVersionUID = 1L;

    protected Integer id;
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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
