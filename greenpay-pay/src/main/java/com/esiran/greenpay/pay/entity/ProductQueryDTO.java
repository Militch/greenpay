package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付产品
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data

public class ProductQueryDTO  {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 支付产品名称
     */
    private String productName;

}
