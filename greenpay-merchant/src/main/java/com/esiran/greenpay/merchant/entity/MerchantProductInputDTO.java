package com.esiran.greenpay.merchant.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * <p>
 * 商户产品
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class MerchantProductInputDTO {
    @NotNull(message = "商户ID不能为空")
    private Integer merchantId;
    @NotBlank(message = "支付类型编码不能为空")
    private String payTypeCode;
    @NotNull(message = "支付产品ID不能为空")
    private Integer productId;
    @NotNull(message = "支付产品编码不能为空")
    private String productCode;
    @NotBlank(message = "支付产品名称不能为空")
    private String productName;
    @NotNull(message = "支付产品类型不能为空")
    private Integer productType;
    @NotNull(message = "接口模式不能为空")
    private Integer interfaceMode;
    private Integer defaultPassageId;
    private Integer defaultPassageAccId;
    @NotNull(message = "商户费率不能为空")
    private BigDecimal rate;
    private String loopPassages;
    @NotNull(message = "状态不能为空")
    private Boolean status;
}
