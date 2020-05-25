package com.esiran.greenpay.bank.pingan.entity;

public enum PATradeCode {

    ONCE_AGENTPAY("KHKF03"),

    QUERY_ONCE_AGENTPAY("KHKF04"),

    QUERY_AMOUNT("4001");

    PATradeCode(String code) {
        this.code = code;
    }
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
