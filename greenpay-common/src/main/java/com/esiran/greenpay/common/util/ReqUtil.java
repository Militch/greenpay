package com.esiran.greenpay.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class ReqUtil {
    public static boolean isView(HttpServletRequest request){
        Boolean isPageViewPost = (Boolean)
                request.getAttribute("_isView");
        if (isPageViewPost == null) return false;
        return isPageViewPost;
    }
    public static void savePostErrors(HttpSession httpSession, List<String> errors){
        httpSession.setAttribute("_errors", errors);
    }

    public static void savePostError(HttpSession httpSession, String error){
        List<String> errors = new ArrayList<>();
        errors.add(error);
        httpSession.setAttribute("_errors", errors);
    }
    public static String buildRedirect(String url){
        return String.format("redirect:%s",url);
    }
    public static String buildRedirect(String url,Object... args){
        return buildRedirect(String.format(url,args));
    }
}
