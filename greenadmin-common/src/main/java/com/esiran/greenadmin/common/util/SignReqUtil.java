package com.esiran.greenadmin.common.util;

import com.esiran.greenadmin.common.sign.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SignReqUtil {
    public static SignType verifySignParam(HttpServletRequest request,long expire) throws IllegalAccessException {
        String timestampStr = request.getParameter("timestamp");
        if (timestampStr == null || timestampStr.length() == 0)
            throw new IllegalArgumentException("请求已过期，或超时");
        long timestamp = Long.parseLong(timestampStr);
        long currentTimestamp = System.currentTimeMillis();
        long timeDiff = currentTimestamp - timestamp;
        if (timeDiff > expire )
            throw new IllegalAccessException("请求已过期，或超时");
        Map<String,String> params = resolveRequestParameter(request);
        String signTypeStr = request.getParameter("signType");
        if (StringUtils.isEmpty(signTypeStr))
            throw new IllegalAccessException("无效的签名方式");
        String principal = MapUtil.sortAndSerialize(params,new String[]{"sign"});
        SignType signType = signTypeStr.equals("md5") ? new Md5SignType(principal)
                : signTypeStr.equals("hmac_md5") ? new HMACMD5SignType(principal)
                : signTypeStr.equals("rsa") ? new RSA2SignType(principal) :null;
        if (signType == null) {
            throw new IllegalAccessException("无效的签名方式");
        }
        return signType;
    }
    public static boolean checkoutSign(SignType signType,String sign, String credential) {
        SignVerify signVerify;
        if (signType instanceof RSA2SignType){
            signVerify = signType.sign(credential);
            sign = UrlSafeB64.decode(sign);
        }else {
            signVerify = signType.sign(credential);
        }
        return !StringUtils.isEmpty(sign) && !signVerify.verify(sign);
    }
    private static Map<String,String> resolveRequestParameter(HttpServletRequest request){
        Enumeration<String> es = request.getParameterNames();
        Map<String,String> map = new HashMap<>();
        while (es.hasMoreElements()){
            String key = es.nextElement();
            String[] values = request.getParameterValues(key);
            map.put(key,values[0]);
        }
        return map;
    }
}
