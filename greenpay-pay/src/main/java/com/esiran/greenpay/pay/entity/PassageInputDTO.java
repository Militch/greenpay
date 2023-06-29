package com.esiran.greenpay.pay.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class PassageInputDTO {
    @NotBlank(message = "通道名称不能为空")
    private String passageName;

    @NotBlank(message = "支付类型不能为空")
    private String payTypeCode;

    @NotBlank(message = "支付接口不能为空")
    private String interfaceCode;

    @NotNull(message = "状态不能为空")
    private Boolean status;

}
