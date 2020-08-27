package com.esiran.greenadmin.common.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class EncryptUtil {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String hmacMd5(String str,String key){
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(),"HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] hash = mac.doFinal(str.getBytes());
            return byte2HexString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String md5(String str){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            return byte2HexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte2HexString(byte[] bytes){
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : bytes) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0").append(Integer.toHexString((0xFF & aHash)));
            } else {
                hexString.append(Integer.toHexString(0xFF & aHash));
            }
        }
        return hexString.toString();
    }
    public static String getRandom(int length){
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val.append(random.nextInt(10));
        }
        return val.toString();
    }
    public static String baseTimelineCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String dateTime = simpleDateFormat.format(new Date());
        return dateTime.concat(getRandom(3));
    }
    public static String[] encode(String str){
        String md5Str = md5(str);
        String[] out = new String[4];
        for (int i=0;i<out.length;i++){
            String s = md5Str.substring(i*8,(i*8)+8);
            long l = Long.parseLong(s,16);
            long fixL = 0x3FFFFFFF&l;
            StringBuilder sb = new StringBuilder();
            for (int j=0;j<6;j++){
                int n = (int) (fixL%62);
                sb.append(CHARS.charAt(n));
                fixL = fixL >> 5;
            }
            out[i] = sb.toString();
        }
        return out;
    }
}
