package com.esiran.greenpay.pay.entity;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 代付订单表

 * </p>
 *
 * @author Militch
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("replacepay_order")
public class ReplacepayOrder extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 代付订单id
     */
    private String replaceId;

    /**
     * 商户id
     */
    private String mchId;

    /**
     * 银行账号名
     */
    @NotEmpty(message = "账户名不能为空")
    private String bankName;

    /**
     * 银行账号
     */
    @NotNull(message = "银行账号不能为空")
    private Long accountNumber;

    /**
     * 代付金额
     */
    @NotNull(message = "代付金额不能为空")
    private Long replaceMoney;

    /**
     * 订单状态 0 待审核 1 审核通过 -1 审核失败
     */
    private Integer status;


    /**
     * 订单结束时间
     */
    private LocalDateTime endAt;


}
