package com.esiran.greenadmin.common.sign;

public interface SignVerify {
    boolean verify(String target);
    String getSign();
}
