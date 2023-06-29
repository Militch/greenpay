package com.esiran.greenpay.common.exception;

import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.esiran.greenpay.common.entity.APIError;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.util.ReqUtil;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response){
        BindingResult bindResult = e.getBindingResult();
        List<APIError> errors = resolveApiErrors(bindResult);
        Map<String,Object> map = new HashMap<>();
        map.put("code","ARGUMENT_NOT_VALID");
        map.put("message", "请求参数校验失败");
        map.put("errors", errors);
        response.setStatus(400);
        return map;
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String,Object> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request,
            HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        map.put("code","ARGUMENT_NOT_VALID");
        map.put("message", "请求参数校验失败");
        response.setStatus(400);
        return map;
    }
    private List<APIError> resolveApiErrors(
            BindingResult bindingResult){
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return fieldErrors.stream().map(item->{
            APIError apiError = new APIError();
            String code = item.getCode();
            String field = item.getField();
            assert code != null;
            apiError.setCode(field.concat(".").concat(code));
            apiError.setMessage(item.getDefaultMessage());
            return apiError;
        }).collect(Collectors.toList());
    }
    @ExceptionHandler(BindException.class)
    public Map<String,Object> handleBindException(
            BindException e, HttpServletResponse response,
            HttpServletRequest request, HttpSession session) throws IOException {
        List<APIError> errors = resolveApiErrors(e.getBindingResult());
        if (ReqUtil.isView(request)){
            List<String> es = errors.stream().map(APIError::getMessage).collect(Collectors.toList());
            ReqUtil.savePostErrors(session,es);
            String s = request.getRequestURI();
            response.sendRedirect(s);
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code","ARGUMENT_NOT_VALID");
        map.put("message", "请求参数校验失败");
        map.put("errors", errors);
        response.setStatus(400);
        return map;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String,Object> handleResourceNotFoundException(
            ResourceNotFoundException e, HttpServletResponse response,
            HttpServletRequest request, HttpSession session) throws IOException {
        if (ReqUtil.isView(request)){
            response.setStatus(404);
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code","RESOURCE_NOT_FOUND");
        map.put("message", "资源不存在");
        response.setStatus(404);
        return map;
    }

    @ExceptionHandler(PostResourceException.class)
    public Map<String,Object> handlePostResourceException(
            PostResourceException e, HttpServletResponse response,
            HttpServletRequest request, HttpSession session) throws IOException {
        if (ReqUtil.isView(request)){
            ReqUtil.savePostError(session,e.getMessage());
            response.setStatus(400);
            String s = request.getRequestURI();
            response.sendRedirect(s);
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code","RESOURCE_NOT_FOUND");
        map.put("message", "资源不存在");
        response.setStatus(404);
        return map;
    }

    @ExceptionHandler(APIException.class)
    public  Map<String,Object> handleAPIException(APIException e,HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        map.put("code",e.getCode());
        map.put("message", e.getMessage());
        response.setStatus(400);
        return map;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public  Map<String,Object> handleAPIException(HttpRequestMethodNotSupportedException e,HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        map.put("code","REQUEST_METHOD_NOT_SUPPORTED");
        map.put("message", e.getMessage());
        response.setStatus(404);
        return map;
    }
//    @ExceptionHandler(Exception.class)
//    public Map<String,Object> handleDefaultException(
//            HttpServletRequest request,
//            Exception e,
//            HttpServletResponse response,
//            HttpSession httpSession) throws IOException {
//        if (ReqUtil.isView(request)){
//            ReqUtil.savePostError(httpSession,e.getMessage());
//            String s = request.getRequestURI();
//            response.sendRedirect(s);
//            return null;
//        }
//        e.printStackTrace();
//        response.setStatus(500);
//        Map<String,Object> map = new HashMap<>();
//        map.put("code","SERVER_ERROR");
//        map.put("message", e.getMessage());
//        return map;
//    }
}
