package com.esiran.greenpay.pay.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PassageAndSubAccount {
    private BigDecimal productRate;
    private Passage passage;
    private PassageAccount passageAccount;

    public PassageAndSubAccount(Passage passage, PassageAccount passageAccount) {
        this.passage = passage;
        this.passageAccount = passageAccount;
    }
}
