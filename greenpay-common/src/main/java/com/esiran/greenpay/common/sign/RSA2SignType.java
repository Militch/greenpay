package com.esiran.greenpay.common.sign;

import com.esiran.greenpay.common.util.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RSA2SignType extends SignTypeAbs {
    private static final Logger logger = LoggerFactory.getLogger(RSA2SignType.class);
    public RSA2SignType(String principal) {
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
        String sign = RSAUtil.sign(principalBytes,credential);
        logger.debug("credential With Rsa2: {}", credential);
        logger.debug("Sign With Rsa2: {}", sign);
        return sign;
    }
}
