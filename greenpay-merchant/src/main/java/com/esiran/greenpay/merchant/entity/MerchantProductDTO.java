package com.esiran.greenpay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.esiran.greenpay.pay.entity.ProductDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商户产品
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class MerchantProductDTO {
    private Integer id;
    private Integer merchantId;
    private String payTypeCode;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer productType;
    private Integer interfaceMode;
    private Integer defaultPassageId;
    private Integer defaultPassageAccId;
    private BigDecimal rate;
    private String rateDisplay;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
