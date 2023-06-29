package com.esiran.greenpay.common.sign;

import com.esiran.greenpay.common.util.EncryptUtil;
import com.esiran.greenpay.common.util.RSAUtil;

import java.nio.charset.StandardCharsets;

import static com.esiran.greenpay.common.util.RSAUtil.SIGNATURE_ALGORITHM_SHA1_WITH_RSA;

public class RSASignType extends SignTypeAbs {
    public RSASignType(String principal) {
        super(principal);
    }

    @Override
    public SignVerify sign(String credential) {
        return new RSASignVerify(getPrincipal(),credential);
    }

    @Override
    public String sign2(String credential) {
        if (getPrincipal() == null || getPrincipal().length() == 0) return null;
        byte[] principalBytes = getPrincipal().getBytes(StandardCharsets.UTF_8);
        return RSAUtil.sign(principalBytes,credential,SIGNATURE_ALGORITHM_SHA1_WITH_RSA);
    }
}
