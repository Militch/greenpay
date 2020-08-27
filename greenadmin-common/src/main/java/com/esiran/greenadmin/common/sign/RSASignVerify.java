package com.esiran.greenadmin.common.sign;

import com.esiran.greenadmin.common.util.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RSASignVerify implements SignVerify{
    private final String sign;
    private final String publicKey;
    private static final Logger logger = LoggerFactory.getLogger(SignVerify.class);
    public RSASignVerify(String sign, String publicKey) {
        this.sign = sign;
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(String target){
        logger.debug("Verify rsa sign target: {}, public: {}",target,publicKey);
        return RSAUtil.verify(sign.getBytes(StandardCharsets.UTF_8),publicKey,target);
    }

    @Override
    public String getSign() {
        return sign;
    }
}
