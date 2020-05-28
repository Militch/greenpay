package com.esiran.greenpay.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.esiran.greenpay.common.entity.BaseMapperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_interface")
public class Interface  extends BaseMapperEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 支付接口编码
     */
    private String interfaceCode;

    /**
     * 支付接口名称
     */
    private String interfaceName;
    /**
     * 应用场景
     */
    private Integer scenarios;
    private Integer interfaceType;
    private String interfaceImpl;
    /**
     * 支付类型编码
     */
    private String payTypeCode;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Boolean status;

}
