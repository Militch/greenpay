package com.esiran.greenpay.openapi.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.entity.BaseSignInput;
import com.esiran.greenpay.common.sign.*;
import com.esiran.greenpay.common.util.MapUtil;
import com.esiran.greenpay.common.util.UrlSafeB64;
import com.esiran.greenpay.common.wrapper.JsonRequestWrapper;
import com.esiran.greenpay.merchant.entity.ApiConfig;
import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.merchant.service.IApiConfigService;
import com.esiran.greenpay.merchant.service.IMerchantService;
import com.esiran.greenpay.openapi.security.OpenAPISecurityUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OPenAPISecurityFilter implements Filter {
    private final static Gson gson = new Gson();
    private final IMerchantService merchantService;
    private final IApiConfigService apiConfigService;
    @Value("${greenpay.openapi.security.sign.enabled:false}")
    private boolean verifySignEnabled;
    @Value("#{'${greenpay.openapi.security.sign.allow_types:}'.split(',')}")
    private List<String> allowSignTypes;
    private final String[] allowedPaths;
    @Autowired
    public OPenAPISecurityFilter(IMerchantService merchantService, IApiConfigService apiConfigService) {
        this.merchantService = merchantService;
        this.apiConfigService = apiConfigService;
        allowedPaths = new String[]{
                "/v1/helper/wx/callback/code($|/$)",
                "/v1/cashiers/pay/wx/order($|/$)",
                "/v1/cashiers/qr/orders/.+($|/$)",
                "/v1/invoices/.+?/callback($|/$)",
                "/v1/cashiers/pages($|/$)",
                "/v1/cashiers/flow($|/$)",
                "/v1/cashiers/flow($|/$)",
                "/.+?\\.css$",
                "/.+?\\.png$",
                "/.+?\\.jpg$",
                "/v1/helper/qr/builder($|/$)"
        };
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    private void printError(HttpServletResponse response, String code, String msg, int status) throws IOException {
        Map<String,String> out = new LinkedHashMap<>();
        out.put("code",code);
        out.put("msg",msg);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(status);
        String body = gson.toJson(out);
        PrintWriter pw = response.getWriter();
        pw.println(body);
        pw.flush();
        pw.close();
    }

    private boolean checkAllowedPaths(String url){
        for (String path : allowedPaths){
            Pattern pattern = Pattern.compile(path);
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()){
                return true;
            }
        }
        return false;
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        servletRequest.setCharacterEncoding("UTF-8");
        String url = httpServletRequest.getRequestURI();
        if (checkAllowedPaths(url)){
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }
        try {
            verifyRequisiteParam(httpServletRequest);
            verifySign(httpServletRequest);
            filterChain.doFilter(httpServletRequest, servletResponse);
        }catch (Exception e){
            if (e instanceof ClassCastException || e instanceof NumberFormatException){
                printError(httpServletResponse,"INVALID_REQUEST","请求参数无效",400);
            }else if(e instanceof APIException){
                APIException apiException = (APIException) e;
                printError(
                        httpServletResponse,
                        apiException.getCode(),
                        e.getMessage(),
                        apiException.getStatus()
                );
            }else {
                printError(httpServletResponse,"UNKNOWN_ERROR","未知错误",500);
                e.printStackTrace();
            }
        }
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


    private static void verifyRequisiteParam(HttpServletRequest request) throws APIException {
        String timestampStr = request.getParameter("timestamp");
        if (timestampStr == null)
            throw new APIException("请求已过期，或超时","INVALID_REQUEST",400);
        long timestamp = Long.parseLong(timestampStr);
        long currentTimestamp = System.currentTimeMillis();
        long timeDiff = currentTimestamp - timestamp;
        if (timeDiff > 600000 )
            throw new APIException("请求已过期，或超时","INVALID_REQUEST",400);
    }

    private void verifySign(HttpServletRequest request) throws APIException {
        Map<String,String> params = resolveRequestParameter(request);
        String signTypeStr = request.getParameter("signType");
        if (StringUtils.isEmpty(signTypeStr))
            throw new APIException("无效的签名方式","INVALID_REQUEST_SIGN_TYPE",400);
        String principal = MapUtil.sortAndSerialize(params,new String[]{"sign"});
        if (!allowSignTypes.contains(signTypeStr)){
            throw new APIException("无效的签名方式","INVALID_REQUEST_SIGN_TYPE",400);
        }
        SignType signType = signTypeStr.equals("md5") ? new Md5SignType(principal)
                : signTypeStr.equals("hmac_md5") ? new HMACMD5SignType(principal)
                : signTypeStr.equals("rsa") ? new RSA2SignType(principal) :null;
        if (signType == null)
            throw new APIException("无效的签名方式","INVALID_REQUEST_SIGN_TYPE",400);
        String apiKey = request.getParameter("apiKey");
        if (StringUtils.isEmpty(apiKey))
            throw new APIException("无效的 API_KEY","INVALID_API_KEY",400);
        LambdaQueryWrapper<ApiConfig> queryWrapper = new QueryWrapper<ApiConfig>()
            .lambda().eq(ApiConfig::getApiKey,apiKey);
        ApiConfig apiConfig = apiConfigService.getOne(queryWrapper);
        if (apiConfig == null)
            throw new APIException("无效的 API_KEY","INVALID_API_KEY",400);
        Merchant merchant = merchantService.getById(apiConfig.getMchId());
        if (merchant == null)
            throw new APIException("无效的 API_KEY","INVALID_API_KEY",400);
        SignVerify signVerify;
        String sign;
        if (signType instanceof RSA2SignType){
            signVerify = signType.sign(apiConfig.getMchPubKey());
            sign = request.getParameter("sign");
            sign = UrlSafeB64.decode(sign);
        }else {
            signVerify = signType.sign(apiConfig.getApiSecurity());
            sign = request.getParameter("sign");
        }
        if (StringUtils.isEmpty(sign))
            throw new APIException("签名内容不能为空","INVALID_REQUEST",400);
        if (verifySignEnabled && !signVerify.verify(sign)) {
            throw new APIException("签名校验失败","INVALID_SIGN",400);
        }
        OpenAPISecurityUtils.setSubject(merchant);
    }

    @Override
    public void destroy() {

    }
}
