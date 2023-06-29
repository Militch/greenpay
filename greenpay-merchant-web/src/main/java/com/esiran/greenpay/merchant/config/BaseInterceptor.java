package com.esiran.greenpay.merchant.config;

import com.esiran.greenpay.merchant.entity.Merchant;
import com.esiran.greenpay.system.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();
        boolean isPage = method.getReturnType().equals(String.class);
        boolean isJosn = method.isAnnotationPresent(ResponseBody.class);
        boolean isController = (!hm.getBeanType().isAnnotationPresent(RestController.class) && hm.getBeanType().isAnnotationPresent(Controller.class));
        request.setAttribute("_isView", isPage && !isJosn && isController);
        try {
            Merchant user = (Merchant) SecurityUtils.getSubject().getPrincipal();
            request.setAttribute("user",user);
        }catch (Exception e){
            return true;
        }
        return true;
    }
}
