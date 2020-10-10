package com.esiran.greenpay.agentpay.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class TripleDes {

    private static final String Algorithm = "DESede"; //定义 加密算法,可用 DES,DESede,Blowfish
    private static final String ENCODING = "UTF-8";


    //加密，使用base64编码
    public static String encrypt(String keybyte, String src) {

        try {

            SecretKey deskey = new SecretKeySpec(Base64.decode(keybyte), Algorithm);

            Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] bts = c1.doFinal(src.getBytes(ENCODING));

            return Base64.encode(bts);

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String keybyte, String src) {

        try {

            SecretKey deskey = new SecretKeySpec(Base64.decode(keybyte), Algorithm);

            Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            c1.init(Cipher.DECRYPT_MODE, deskey);

            byte[] bts = c1.doFinal(Base64.decode(src));

            return new String(bts,ENCODING);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPubKeyDER(String derPath){
        String pub_key = "" ;
        try {
            InputStream streamCert = new java.io.FileInputStream( derPath);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cert = factory.generateCertificate(streamCert);
            pub_key = Base64.encode(cert.getPublicKey().getEncoded()) ;
            streamCert.close();
            System.out.println( pub_key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pub_key;
    }

    public static String getPubKeyCER(String cerPath){
        String pub_key = "" ;
        try {
            InputStream streamCert = new java.io.FileInputStream( cerPath);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cert = factory.generateCertificate(streamCert);
            pub_key = Base64.encode(cert.getPublicKey().getEncoded()) ;
            streamCert.close();
            System.out.println( pub_key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pub_key;
    }

    public static void main(String[] args) throws Exception {
        String dna_pub_key =      "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqWSfUW3fSyoOYzOG8joy3xldpBanLVg8gEDcvm9KxVjqvA/qJI7y0Rmkc1I7l9vAfWtNzphMC+wlulpaAsa/4PbfVj+WhoNQyhG+m4sP27BA8xuevNT9/W7/2ZVk4324NSowwWkaqo1yuZe1wQMcVhROz2h+g7j/uZD0fiCokWwIDAQAB";
        String merchant_pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqWSfUW3fSyoOYzOG8joy3xldpBanLVg8gEDcvm9KxVjqvA/qJI7y0Rmkc1I7l9vAfWtNzphMC+wlulpaAsa/4PbfVj+WhoNQyhG+m4sP27BA8xuevNT9/W7/2ZVk4324NSowwWkaqo1yuZe1wQMcVhROz2h+g7j/uZD0fiCokWwIDAQAB";

        String merchant_pfx_key = "C:\\yilian.pfx";
        String key_pass = "11111111";


//		getPubKeyCER("D:\\easypay\\project\\代收付\\代收付系统\\商户上线\\深圳市宏享科技有限公司\\312000100999371-Signature.der");
        getPubKeyCER("C:\\Users\\user\\Desktop\\证书\\806000101883606-Signature.der");

        if(true)
            return;
//        System.out.println(Base64.encode(RSA.getPublicKey("D:\\gdyilian_merchant_signature.pfx", "80115864").getEncoded()));
//        System.out.println(Base64.encode(RSA.getPublicKey("C:\\yilian.pfx", "11111111").getEncoded()));
//        System.out.println(Base64.encode(RSA.getPublicKey("D:\\easypay\\project\\代收付\\代收付系统\\接口接入发给商户资料\\librsa\\cert\\RSA_merchant_test.pfx", "000000").getEncoded()));
//        System.out.println(Base64.encode(RSA.getPublicKey("C:\\Users\\user\\Desktop\\证书\\806000101883606-Signature.pfx", "71112911").getEncoded()));
        if(true)
            return;

        //24key加密
        String key = "0F91F44FB331EB39779F9605";
        String key_f = RSA.encrypt(Base64.encode(key.getBytes()), dna_pub_key);
        System.out.println("key密文="+key_f);
        //24key解密
        String key_enc = "QhX+0f8JPqicd2HjJbM3p/A5aQw/xeAI/5G0bCirhy8zKCTNlUAAdF/mWx+lgvxOoBTJdGB30VZGZ+PGUN2elU1B8BVPgie9qEe/5WTfN4Ze9UEPtHdqFoaIWxE6vUcX7oiMUjn+veYJB2tRFtBrx9HKNvoo/coap4wMxq6KG0o=";
        String key_g = RSA.decrypt(key_enc, merchant_pfx_key, key_pass);
        System.out.println("key="+key_g);

        if(true)
            return;


//		String a = "<MSGBEAN><MSG_TYPE>200002</MSG_TYPE><BATCH_NO>99EE936559D776</BATCH_NO><USER_NAME>13760136514</USER_NAME><TRANS_STATE></TRANS_STATE><MSG_SIGN></MSG_SIGN><TRANS_DETAILS></TRANS_DETAILS></MSGBEAN>";
        String a = "99EE936559W345 13760136514 200002";
        System.out.println("报文="+a);


        //签名
        String b = RSA.sign(a,merchant_pfx_key,key_pass);
        System.out.println("签名="+b);


        //验签
        boolean c = RSA.verify(b,merchant_pub_key,a);
        System.out.println("验签="+c);

        //加密
//    	String d = Util.generateKey(9999,24);
        String d = "M0ZDOUE1MkVFMEJDMzU2MDFGMzlGQjVF";
        System.out.println("key="+d);
        String e = TripleDes.encrypt(d, a);
        System.out.println("报文密文="+e);
        String e1 = TripleDes.encrypt(d, b);
        System.out.println("签名密文="+e1);

        String f = RSA.encrypt(d, merchant_pub_key);
        System.out.println("key密文="+f);

        //解密
//		f="julla2HwxAGQjAIekO8JbKNYhmIVNv4urfuSoDxBVRGogRN29IDmu1HbLd1Rwua85vkdrGeilmT9BU1Wn0MKnd1eg4wf5FQX3zA1UVxzlJmfQjvjsic8C/96e1Sq/V8VvOnYJX5O+s0i4pD0JVX2KuOicmL6xzBZhTFYbRS1VH8=";
        String g = RSA.decrypt(f, merchant_pfx_key, key_pass);
        System.out.println("key="+g);

        String h = TripleDes.decrypt(g, e);
        System.out.println("报文="+h);
        String h1 = TripleDes.decrypt(g, e1);
        System.out.println("签名="+h1);

        System.out.println(a.equals(h));
        System.out.println(b.equals(h1));
    }
}
