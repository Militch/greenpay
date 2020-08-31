package com.esiran.greenadmin.common.entity;


import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;

public class APIException extends Exception{
    private String code;
    private Integer status;
    public APIException(String message, String code) {
        super(message);
        this.code = code;
    }
    public APIException(String message, String code,Integer status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
