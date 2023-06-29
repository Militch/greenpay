package com.esiran.greenpay.openapi.security;

import com.esiran.greenpay.merchant.entity.Merchant;

public class OpenAPISecurityUtils {
    private final static ThreadLocal<Merchant> tls = new ThreadLocal<>();
    public static Merchant getSubject(){
        return tls.get();
    }
    public static void setSubject(Merchant m){
        tls.set(m);
    }
}
