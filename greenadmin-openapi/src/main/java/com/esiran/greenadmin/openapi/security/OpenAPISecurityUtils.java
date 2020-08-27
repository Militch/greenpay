package com.esiran.greenadmin.openapi.security;

public class OpenAPISecurityUtils {
    private final static ThreadLocal<Object> tls = new ThreadLocal<>();
    public static Object getSubject(){
        return tls.get();
    }
    public static void setSubject(Object m){
        tls.set(m);
    }
}
