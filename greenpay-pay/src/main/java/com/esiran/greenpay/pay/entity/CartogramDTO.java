package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @Package com.esiran.greenpay.pay.entity
 * @date 2020/5/23 18:11
 */
@Data
public class CartogramDTO {

    private String name;

    private Date time;

    private Integer count;
    private Integer successCount;

    private Long amount;
    private Long successAmount;
}
