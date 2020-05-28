package com.esiran.greenpay.pay.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InterfaceInputDTO {
    @NotBlank(message = "接口编码不能为空")
    private String interfaceCode;
    @NotBlank(message = "接口名称不能为空")
    private String interfaceName;
    @NotBlank(message = "支付类型不能为空")
    private String payTypeCode;
    @NotNull(message = "应用场景不能为空")
    private Integer scenarios;
    @NotNull(message = "接口调用方式不能为空")
    private Integer interfaceType;
    private String interfaceImpl;
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
