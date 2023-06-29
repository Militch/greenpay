package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付产品通道子账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class ProductPassageDTO {
    private Integer id;
    private Integer productId;
    private Integer passageId;
    private Integer widget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
