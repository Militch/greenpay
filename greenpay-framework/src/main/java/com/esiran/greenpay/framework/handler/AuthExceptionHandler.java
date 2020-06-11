package com.esiran.greenpay.framework.handler;

import com.esiran.greenpay.common.util.ReqUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorized(){
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
