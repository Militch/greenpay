package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付通道
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Data
public class PassageQueryDTO {

    private Integer id;

    /**
     * 支付通道名称
     */
    private String passageName;

    /**
     * 支付类型编码
     */
    private String payTypeCode;


}
