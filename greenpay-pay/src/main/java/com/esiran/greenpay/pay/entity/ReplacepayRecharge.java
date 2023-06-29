package com.esiran.greenpay.pay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 商户充值订单表
 * </p>
 *
 * @author Militch
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("replacepay_recharge")
public class ReplacepayRecharge extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 代付充值id
     */
    private String rechargeId;

    /**
     * 商户id
     */
    private String mchId;

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    private Long rechargeMoney;

    /**
     * 充值订单状态 0 待审核 1 充值成功 -1 未充值
     */
    private Integer status;


    /**
     * 结束时间
     */
    private LocalDateTime endTime;


}
