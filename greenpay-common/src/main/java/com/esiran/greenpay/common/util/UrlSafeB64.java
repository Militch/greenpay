package com.esiran.greenpay.common.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UrlSafeB64 {
    public static String encode(String content){
        return content.replaceAll("\\+","*")
                .replaceAll("/","-")
                .replaceAll("=",".");
    }
    public static String decode(String content){
        return content.replaceAll("\\*","+")
                .replaceAll("-","/")
                .replaceAll("\\.","=");
    }
}
