package com.esiran.greenpay.merchant.entity;

import lombok.Data;

@Data
public class HomeData {
    private PayAccountDTO payAccountDTO;

    private PrepaidAccountDTO prepaidAccountDTO;

    private Integer totalCount;

    private Integer successCount;

    private String totalMoney;

    private String successMoney;


    private Integer intraDayOrdersCount;

    private String intrDayOrderAmounts;

    private String intrDayOrderAmountSucces;

    private String yesterDayOrdersAmountSucces;
}
