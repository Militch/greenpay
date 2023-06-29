package com.esiran.greenpay.common.sign;

import com.esiran.greenpay.common.util.EncryptUtil;

public interface SignType {
    SignVerify sign(String credential);
    String sign2(String credential);
}
