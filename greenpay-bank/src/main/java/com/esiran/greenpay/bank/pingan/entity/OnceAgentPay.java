package com.esiran.greenpay.bank.pingan.entity;

import lombok.Data;

/**
 * 单笔代付
 */
@Data
public class OnceAgentPay {

    //订单号
    private String orderNumber;
    //企业签约帐号
    private String acctNo;
    //金额
    private String tranAmount;
    //收款卡号
    private String inAcctNo;
    //收款户名
    private String inAcctName;


}
