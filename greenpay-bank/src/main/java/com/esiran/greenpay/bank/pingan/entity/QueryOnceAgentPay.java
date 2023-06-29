package com.esiran.greenpay.bank.pingan.entity;

import lombok.Data;

/**
 * 单笔代付查询
 */
@Data
public class QueryOnceAgentPay {

    //企业签约帐号
    private String acctNo;
    //订单号
    private String orderNumber;

}
