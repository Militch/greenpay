package com.esiran.greenadmin.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TOTPUtil {
    private static byte[] long2bytes(long val){
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (val & 0xFF);
            val >>= 8;
        }
        return result;
    }
    private static byte[] hmacSHA256(String key, long val) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec sk = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance(sk.getAlgorithm());
        mac.init(sk);
        return mac.doFinal(long2bytes(val));
    }
    private static byte[] hmacSHA1(String key, long val) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec sk = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance(sk.getAlgorithm());
        mac.init(sk);
        return mac.doFinal(long2bytes(val));
    }
    private static String padZero2start(long number, int len){
        return ("0000000000" + number).substring(("0000000000" + number).length()-len);
    }
    /**
     * 生成基于时间序列的安全码
     * @param key 密钥
     * @param limit 时间限制/s
     * @return 安全码
     */
    public static String nextCode(String key, int limit, String algorithm) throws Exception {
        // 获取当前时间
        long current = System.currentTimeMillis();
        // 计数器
        long count = (long) Math.floor(current/(limit*1000.00));
        byte[] bytes;
        if (algorithm == null || algorithm.length() == 0)
            throw new IllegalArgumentException("algorithm Not Supported");
        if (algorithm.equals("SHA1")){
            bytes = hmacSHA1(key, count);
        }else if(algorithm.equals("SHA256")){
            bytes = hmacSHA256(key, count);
        }else {
            throw new IllegalArgumentException(algorithm + "algorithm Not Supported");
        }
        // 获取最后一个整型数值
        int lastNum = (bytes[bytes.length-1] & 0xff);
        // 将最后数值的低四位作为偏移量
        int offset = lastNum & 0xf;
        long num = (bytes[offset] & 0x7f) << 24 |
                ((bytes[offset + 1] & 0xff) << 16) |
                ((bytes[offset + 2] & 0xff) << 8) |
                (bytes[offset + 3] & 0xff);
        return padZero2start((int) Math.floor(num%1000000.00),6);
    }

    public static String gaUri(String account, String secretKey, String issuer) throws UnsupportedEncodingException {
        if (account == null || account.length() == 0) return null;
        if (secretKey == null || secretKey.length() == 0) return null;
        if (issuer == null || issuer.length() == 0) return null;
        String accountEncoded =  URLEncoder.encode(String.format("%s:%s",issuer,account), "UTF-8")
                .replaceAll("\\+","%20");
        String secretKeyEncoded = URLEncoder.encode(secretKey, "UTF-8")
                .replaceAll("\\+","%20");
        String issuerEncoded = URLEncoder.encode(issuer, "UTF-8")
                .replaceAll("\\+","%20");
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s",accountEncoded,secretKeyEncoded,issuerEncoded);
    }
}
