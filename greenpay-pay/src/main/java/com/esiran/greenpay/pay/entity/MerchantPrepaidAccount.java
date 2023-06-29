package com.esiran.greenpay.pay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户预充值账户
 * </p>
 *
 * @author Militch
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MerchantPrepaidAccount extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 商户ID
     */
    private Integer merchantId;

    /**
     * 可用余额（分）
     */
    private Integer availBalance;

    /**
     * 冻结金额（分）
     */
    private Integer freezeBalance;



}
