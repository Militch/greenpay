package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @Package com.esiran.greenpay.pay.entity
 * @date 2020/5/23 18:11
 */
@Data
public class CartogramPayDTO {


    private String payname;


    private Long count;


    private Long amount;
}
