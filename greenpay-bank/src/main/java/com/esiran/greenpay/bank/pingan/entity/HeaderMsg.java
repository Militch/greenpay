package com.esiran.greenpay.bank.pingan.entity;


import lombok.Data;

/**
 * 报文头数据
 */
@Data
public class HeaderMsg {
    //企业代码
    private String companyCode;
    //系统流水号 20位
    private String outOrderNumber;
}
