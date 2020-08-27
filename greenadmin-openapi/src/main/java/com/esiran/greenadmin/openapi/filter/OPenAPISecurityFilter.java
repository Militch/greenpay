package com.esiran.greenadmin.openapi.filter;

import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.sign.*;
import com.esiran.greenadmin.common.util.MapUtil;
import com.esiran.greenadmin.common.util.UrlSafeB64;
import com.esiran.greenadmin.openapi.security.OpenAPISecurityUtils;
import com.google.gson.Gson;
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
    @Value("${greenadmin.openapi.security.sign.enabled:false}")
    private boolean verifySignEnabled;
    @Value("#{'${greenadmin.openapi.security.sign.allow_types:}'.split(',')}")
    private List<String> allowSignTypes;
    private final String[] allowedPaths;
    @Autowired
    public OPenAPISecurityFilter() {
        allowedPaths = new String[]{
                "/v1/helper/wx/openid($|/$)",
                "/v1/helper/wx/callback/code($|/$)",
                "/v1/cashiers/pay/wx/order($|/$)",
                "/v1/cashiers/qr/orders/.+($|/$)",
                "/v1/cashiers/wx_pub/orders/.+($|/$)",
                "/v1/invoices/.+?/callback($|/$)",
                "/v1/cashiers/pages($|/$)",
                "/v1/cashiers/flow($|/$)",
                "/v1/cashiers/flow($|/$)",
                "/v1/cashiers/query/orderStatus($|/$)",
                "/v1/cashiers/success/.+($|/$)",
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
    }

    @Override
    public void destroy() {

    }
}
