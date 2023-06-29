package com.esiran.greenpay.common.sign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SignTypeAbs implements SignType {
    private String principal;
    private static final Logger logger = LoggerFactory.getLogger(SignType.class);
    public SignTypeAbs(String principal){
        logger.debug("Initial SignType principal: {}",principal);
        this.principal = principal;
    }

    public String getPrincipal() {
        return principal;
    }

}
