package com.esiran.greenpay.merchant.entity;

import lombok.Data;

/**
 * @author han
 * @Package com.esiran.greenpay.admin.entity
 * @date 2020/5/23 9:38
 */
@Data
public class HomeDateVo {

    //入驻商户数
    private  Integer merchantUserInteger;

    //收单总数
    private Integer orderTotal;

    //成交总额
    private String orderMoneyTotal ;

    //平台结算支出（元）
    private String paySettleSum;


    //今日收单总数
    private  Integer intradayOrder;

    //今日成交笔数
    private Long intradayOrderSucces;

    //今日成交额
    private String intradayOrderMoneys;

    //今日结算支出
    private String intradaySettleSucces;



}
