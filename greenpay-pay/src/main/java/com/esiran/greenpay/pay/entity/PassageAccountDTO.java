package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付通道账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class PassageAccountDTO {
    private Integer id;
    private String payTypeCode;
    private String passageName;
    private Integer passageId;
    private String accountName;
    private String interfaceAttr;
    private String interfaceAttrDisplay;
    private Boolean status;
    /**
     * 轮训权重
     */
    private Integer weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
