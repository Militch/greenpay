package com.esiran.greenpay.common.sign;

import com.esiran.greenpay.common.util.EncryptUtil;

public class HMACMD5SignType extends SignTypeAbs {
    public HMACMD5SignType(String principal) {
        super(principal);
    }

    @Override
    public SignVerify sign(String credential) {
        return new SimpleSignVerify(
                EncryptUtil.hmacMd5(this.getPrincipal(),credential)
        );
    }

    @Override
    public String sign2(String credential) {
        return EncryptUtil.hmacMd5(this.getPrincipal(),credential);
    }
}
