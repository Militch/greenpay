package com.esiran.greenpay.pay.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 支付类型
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class TypeInputDTO {

    @NotBlank(message = "支付类型编码不能为空")
    private String typeCode;

    @NotBlank(message = "支付类型名称不能为空")
    private String typeName;
    @NotNull(message = "支付类别不能为空")
    private Integer type;

    @NotNull(message = "状态不能为空")
    private Boolean status;
}
