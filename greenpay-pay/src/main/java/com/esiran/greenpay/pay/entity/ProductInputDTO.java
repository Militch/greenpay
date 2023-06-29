package com.esiran.greenpay.pay.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ProductInputDTO {
    @NotBlank(message = "产品编码不能为空")
    private String productCode;
    @NotBlank(message = "产品名称不能为空")
    private String productName;
    @NotNull(message = "产品类型不能为空")
    private Integer productType;
    @NotBlank(message = "支付类型不能为空")
    private String payTypeCode;
    private Integer interfaceMode;
    private Integer defaultPassageId;
    private String loopPassages;
    private Integer defaultPassageAccId;
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
