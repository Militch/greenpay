package com.esiran.greenadmin.framework.handler;

import com.esiran.greenadmin.common.util.ReqUtil;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(ShiroException.class)
    @ResponseBody
    public String handleShiroException(ShiroException e) {
        String simpleName = e.getClass().getSimpleName();
        System.out.println("shiro执行出错：{}".concat(simpleName));
        return  e.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorized(
            UnauthorizedException e,
            HttpServletRequest request,
            HttpSession httpSession){
        List<String> errors = new ArrayList<>();
        if (e instanceof UnauthorizedException) {
            errors.add("当前用户权限不足");
        }

        if (ReqUtil.isView(request)) {
            ReqUtil.savePostErrors(httpSession, errors);
//            String s = request.getRequestURI();
            return ReqUtil.buildRedirect("/unAuth");
        }
        return "";
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(
            AuthenticationException e,
            HttpServletRequest request,
            HttpSession httpSession){
        List<String> errors = new ArrayList<>();
        if (e instanceof IncorrectCredentialsException){
            errors.add("用户名或密码错误");
        }else if (e instanceof UnknownAccountException){
            errors.add("用户名或密码错误");
        } else {
            errors.add(e.getMessage());
        }
        if(ReqUtil.isView(request)){
            ReqUtil.savePostErrors(httpSession,errors);
            String s = request.getRequestURI();
            return ReqUtil.buildRedirect(s);
        }
        return "";
    }
}
