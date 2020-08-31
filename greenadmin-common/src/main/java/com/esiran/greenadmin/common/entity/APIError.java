package com.esiran.greenadmin.common.entity;

import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import lombok.Data;

import java.util.List;

@Data
public class APIError {
    public enum APIErrorCode {
        ARGUMENT_NOT_VALID("ARGUMENT_NOT_VALID", "请求参数错误"),
        RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "请求资源不存在"),
        USERNAME_OR_PASSWORD_NOT_VALID("USERNAME_OR_PASSWORD_NOT_VALID", "账号或密码错误"),
        UNAUTHORIZED("UNAUTHORIZED", "无权限访问此操作"),
        REQUEST_METHOD_NOT_SUPPORTED("REQUEST_METHOD_NOT_SUPPORTED", "请求方法不支持");
        private final String code;
        private final String message;
        APIErrorCode(String code, String message){
            this.code = code;
            this.message = message;
        }
        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
    private String code;
    private String message;
    private List<ErrorDetail> errors;
    public APIError(String code, String message) {
        this(code,message,null);
    }
    public APIError(String code, String message,List<ErrorDetail> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
    public static APIError of(APIErrorCode code){
        return of(code,null);
    }
    public static APIError of(APIErrorCode code,List<ErrorDetail> errors){
        return new APIError(code.getCode(),code.getMessage(),errors);
    }
}
