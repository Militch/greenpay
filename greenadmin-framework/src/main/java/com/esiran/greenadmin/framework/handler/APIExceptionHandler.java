package com.esiran.greenadmin.framework.handler;

import com.esiran.greenadmin.common.entity.APIError;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.entity.ErrorDetail;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.common.exception.ResourceNotFoundException;
import com.esiran.greenadmin.common.util.ReqUtil;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleMethodArgumentNotValidException(
            HttpServletRequest request, HttpSession httpSession,
            MethodArgumentNotValidException e, HttpServletResponse response) throws IOException {
        BindingResult bindResult = e.getBindingResult();
        List<ErrorDetail> errors = resolveApiErrors(bindResult);
        if (ReqUtil.isView(request)){
            List<String> es = errors.stream().map(ErrorDetail::getMessage).collect(Collectors.toList());
            ReqUtil.savePostErrors(httpSession,es);
            String s = request.getRequestURI();
            response.sendRedirect(s);
            return null;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIError.of(APIError.APIErrorCode.ARGUMENT_NOT_VALID, errors));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request,
            HttpServletResponse response){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIError.of(APIError.APIErrorCode.ARGUMENT_NOT_VALID, null));
    }
    private List<ErrorDetail> resolveApiErrors(
            BindingResult bindingResult){
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return fieldErrors.stream().map(item->{
            ErrorDetail apiError = new ErrorDetail();
            String code = item.getCode();
            String field = item.getField();
            if (code == null) code = "UNKNOWN_ERROR";
            apiError.setCode(field.concat(".").concat(code));
            apiError.setMessage(item.getDefaultMessage());
            return new ErrorDetail(field.concat(".").concat(code),
                    item.getDefaultMessage());
        }).collect(Collectors.toList());
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIError> handleBindException(
            BindException e, HttpServletResponse response,
            HttpServletRequest request, HttpSession session) throws IOException {
        List<ErrorDetail> errors = resolveApiErrors(e.getBindingResult());
        if (ReqUtil.isView(request)){
            List<String> es = errors.stream().map(ErrorDetail::getMessage).collect(Collectors.toList());
            ReqUtil.savePostErrors(session,es);
            String s = request.getRequestURI();
            response.sendRedirect(s);
            return null;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIError.of(APIError.APIErrorCode.ARGUMENT_NOT_VALID,
                        errors));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIError> handleAuthenticationException(
            AuthenticationException e,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {
        if (ReqUtil.isView(request)){
            List<String> es = new ArrayList<>();
            es.add("账号或密码校验失败，请重试");
            ReqUtil.savePostErrors(httpSession,es);
            String s = request.getRequestURI();
            response.sendRedirect(s);
            return null;
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(APIError.of(APIError.APIErrorCode.USERNAME_OR_PASSWORD_NOT_VALID));
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<APIError> unauthorized(
            UnauthorizedException e,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (ReqUtil.isView(request)){
            response.sendRedirect("/403");
            return null;
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(APIError.of(APIError.APIErrorCode.UNAUTHORIZED));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIError> handleResourceNotFoundException(
            ResourceNotFoundException e, HttpServletResponse response,
            HttpServletRequest request, HttpSession session) throws IOException {
        if (ReqUtil.isView(request)){
            response.setStatus(404);
            return null;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIError.of(APIError.APIErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(PostResourceException.class)
    public ResponseEntity<APIError> handlePostResourceException(
            PostResourceException e, HttpServletResponse response,
            HttpServletRequest request, HttpSession session) throws IOException {
        if (ReqUtil.isView(request)){
            ReqUtil.savePostError(session,e.getMessage());
            response.setStatus(400);
            String s = request.getRequestURI();
            response.sendRedirect(s);
            return null;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIError.of(APIError.APIErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIError> handleAPIException(APIException e,HttpServletResponse response){
        Integer status = e.getStatus();
        return ResponseEntity.status((status == null ? 400 : status))
                .body(new APIError(e.getCode(),e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIError> handleAPIException(HttpRequestMethodNotSupportedException e,HttpServletResponse response){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIError.of(APIError.APIErrorCode.REQUEST_METHOD_NOT_SUPPORTED));
    }
}
