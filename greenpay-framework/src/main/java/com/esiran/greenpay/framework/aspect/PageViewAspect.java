package com.esiran.greenpay.framework.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Order(1)
@Component
public class PageViewAspect {
    @Pointcut("@annotation(com.esiran.greenpay.framework.annotation.PageViewHandleError)")
    public void pageViewHandleError(){}
    private static HttpServletRequest getRequest(){
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) return null;
        return servletRequestAttributes.getRequest();
    }

    private static boolean isView(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) return false;
        MethodSignature methodSignature = (MethodSignature) signature;
        Class<?> returnType = methodSignature.getReturnType();
        if (!returnType.equals(String.class))
            return false;
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(ResponseBody.class);
    }
    @Before("pageViewHandleError()")
    @SuppressWarnings("unchecked")
    public void doBeforePageViewHandelError(JoinPoint joinPoint){
        if (!isView(joinPoint))
            return;
        HttpServletRequest request = getRequest();
        if (request == null) return;
        HttpSession session = request.getSession();
        List<String> errors = (List<String>)
                session.getAttribute("_errors");
        request.setAttribute("_errors",errors);
        session.removeAttribute("_errors");
    }
}
