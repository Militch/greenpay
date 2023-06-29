package com.esiran.greenpay.agentpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author Militch
 * @since 2020-06-05
 */
@Data
public class PassageRiskInputDTO extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 单笔代付最低金额
     */
    private Integer amountMin;
    @NotNull(message = "单笔最低金额不能为空")
    private String amountMinDisplay;

    /**
     * 单笔代付最大金额
     */
    private Integer amountMax;
    @NotNull(message = "单笔最高金额不能为空")
    private String amountMaxDisplay;

    /**
     * 0 关闭 1 开启
     */
    private Integer status;


}
