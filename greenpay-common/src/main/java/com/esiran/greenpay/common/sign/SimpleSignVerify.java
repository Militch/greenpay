package com.esiran.greenpay.common.sign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSignVerify implements SignVerify {
    private final String sign;
    private static final Logger logger = LoggerFactory.getLogger(SignVerify.class);
    public SimpleSignVerify(String sign){
        this.sign = sign;
    }
    @Override
    public boolean verify(String target){
        logger.debug("Verify simple sign target: {}",target);
        return sign.equals(target);
    }
    @Override
    public String getSign() {
        return sign;
    }
}
