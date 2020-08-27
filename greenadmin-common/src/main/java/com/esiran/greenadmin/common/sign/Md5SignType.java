package com.esiran.greenadmin.common.sign;

import com.esiran.greenadmin.common.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Md5SignType extends SignTypeAbs{
    private static final Logger logger = LoggerFactory.getLogger(Md5SignType.class);
    public Md5SignType(String principal) {
        super(principal);
    }

    @Override
    public SignVerify sign(String credential) {
        return new SimpleSignVerify(
                EncryptUtil.md5(this.getPrincipal().concat(credential))
        );
    }

    @Override
    public String sign2(String credential) {
        String str = this.getPrincipal().concat(credential);
        logger.info("Sign str: {}", str);
        return EncryptUtil.md5(str);
    }
}
