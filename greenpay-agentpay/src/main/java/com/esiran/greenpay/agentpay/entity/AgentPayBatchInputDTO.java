package com.esiran.greenpay.agentpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 代付批次
 * </p>
 *
 * @author Militch
 * @since 2020-05-06
 */
@Data
public class AgentPayBatchInputDTO {



    /**
     * 总金额（单位：分）
     */
    @NotNull(message = "总金额不能为空")
    private Integer totalAmount;

    /**
     * 总笔数
     */
    @NotNull(message = "总笔数不能为空")
    private Integer totalCount;

    /**
     * 订单回调地址
     */
    private String notifyUrl;

    /**
     * 扩展参数
     */
    private String extra;

}
