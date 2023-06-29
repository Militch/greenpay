package com.esiran.greenpay.common.sign;

public interface SignVerify {
    boolean verify(String target);
    String getSign();
}
