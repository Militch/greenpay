package com.esiran.greenadmin.common.util;

import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class RSAUtil {
    private static final String KEY_ALGORITHM = "rsa";
    private static final int DEFAULT_KEY_SIZE = 2048;
    public static final String SIGNATURE_ALGORITHM_MD5_WITH_RSA = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA256_WITH_RSA = "SHA256WithRSA";
    public static final String SIGNATURE_ALGORITHM_SHA1_WITH_RSA = "SHA1WithRSA";

    public static final String PEM_FILE_PUBLIC_PKCS1_BEGIN = "-----BEGIN PUBLIC KEY-----";
    public static final String PEM_FILE_PUBLIC_PKCS1_END = "-----END PUBLIC KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS1_BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS1_END = "-----END RSA PRIVATE KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS8_BEGIN = "-----BEGIN PRIVATE KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS8_END = "-----END PRIVATE KEY-----";

    public static String resolvePublicKey(String content) throws Exception {
        return resolveContentBody(content,PEM_FILE_PUBLIC_PKCS1_BEGIN,PEM_FILE_PUBLIC_PKCS1_END);
    }
    private static List<String> resolveContentLines(String content){
        String[] lines = content.split("(\r\n|\n)");
        List<String> lineList = Arrays.asList(lines);
        return lineList.stream().filter(item-> !StringUtils.isEmpty(item)).collect(Collectors.toList());
    }
    private static String resolveContentBody(String content, String beginLine, String endLine) throws Exception {
        List<String> contentLine = resolveContentLines(content);
        int beginLineIndex = ListUtil.findIndex(contentLine,beginLine);
        int endLineIndex = ListUtil.findIndex(contentLine,endLine);
        boolean not = beginLineIndex == -1 || endLineIndex == -1;
        if (not || beginLineIndex >= endLineIndex)
            throw new Exception("格式校验失败");
        StringBuilder sb = new StringBuilder();
        for (int i=beginLineIndex+1; i < endLineIndex; i++){
            sb.append(contentLine.get(i));
        }
        return sb.toString();
    }
    public static String resolvePrivateKey(String content) throws Exception {
        return resolveContentBody(content,PEM_FILE_PRIVATE_PKCS8_BEGIN,PEM_FILE_PRIVATE_PKCS8_END);
    }

    public static String formatKeyPem(String beginLine, String key,String endLine){
        if (key == null || key.length() == 0) return null;
        byte[] bs = key.getBytes();
        StringBuilder sb = new StringBuilder();
        sb.append(beginLine).append("\r\n");
        for (int i=0; i<bs.length;i++){
            char c = (char) bs[i];
            sb.append(i%64 == 0 && i > 0?"\r\n":"").append(c);
        }
        sb.append("\r\n").append(endLine);
        return sb.toString();
    }
    /**
     * 生成 1024 长度的密钥对
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(){
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }


    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return (Base64.getEncoder().encodeToString(bytes));
    }

    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return (Base64.getEncoder().encodeToString(bytes));
    }
    /**
     * 生成指定长度的密钥对
     * @param keySize 长度
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(int keySize){
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(keySize);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * 私钥签名
     * @param data 签名数据
     * @param privateKey 私钥
     * @param algorithm 签名算法
     * @return 签名字符串
     */
    public static String sign(byte[] data, String privateKey, String algorithm){
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        // 构造 PKCS#8 解码对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            // 构造 RSA 密钥工厂对象
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 生成私钥对象
            PrivateKey privateK = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            // 构造签名对象，并签名数据源
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateK);
            signature.update(data);
            byte[] bytes = signature.sign();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 私钥签名-SHA256WithRSA
     * @param data 签名数据
     * @param privateKey 私钥
     * @return 签名字符串
     */
    public static String sign(byte[] data, String privateKey){
        return sign(data,privateKey,SIGNATURE_ALGORITHM_SHA256_WITH_RSA);
    }
    /**
     * 校验签名-公钥
     * @param data 待校验数据
     * @param publicKey 公钥
     * @param sign 签名
     * @param algorithm 加密算法
     * @return 结果
     */
    public static boolean verify(byte[] data, String publicKey, String sign, String algorithm){
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        // 构造 X509 编码对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            // 构造 RSA 密钥工厂对象
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 生成公钥对象
            PublicKey publicK = keyFactory.generatePublic(x509EncodedKeySpec);
            // 构造签名对象，并校验签名
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicK);
            signature.update(data);
            // 因为签名的时候输出为 BASE64 编码格式，所以需要解码
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
//            e.printStackTrace();
            return false;
        }
//        return false;
    }

    /**
     * 校验签名-公钥-SHA256WithRSA
     * @param data 待校验数据
     * @param publicKey 公钥
     * @param sign 签名
     * @return 结果
     */
    public static boolean verify(byte[] data, String publicKey, String sign){
        return verify(data,publicKey,sign,SIGNATURE_ALGORITHM_SHA256_WITH_RSA);
    }
    /**
     * 校验签名-证书
     * @param data 待校验数据
     * @param cer 证书
     * @param sign 签名
     * @return 结果
     */
    public static boolean verifyWithCertificate(byte[] data, String cer, String sign,String algorithm){
        String publicKey = parsePublicKeyWithCer(cer);
        return verify(data,publicKey,sign,algorithm);
    }
    /**
     * 校验签名-证书-SHA256WithRSA
     * @param data 待校验数据
     * @param cer 证书
     * @param sign 签名
     * @return 结果
     */
    public static boolean verifyWithCertificate(byte[] data, String cer, String sign){
        String publicKey = parsePublicKeyWithCer(cer);
        return verify(data,publicKey,sign,SIGNATURE_ALGORITHM_SHA256_WITH_RSA);
    }
    /**
     * 从证书数据中解析出公钥
     * @param cer 证书字符串
     * @return 公钥
     */
    private static String parsePublicKeyWithCer(String cer){
        byte[] cerBytes = Base64.getDecoder().decode(cer);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(cerBytes));
            PublicKey publicKey = certificate.getPublicKey();
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
