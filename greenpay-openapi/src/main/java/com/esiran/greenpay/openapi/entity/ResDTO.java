package com.esiran.greenpay.openapi.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ResDTO<T> {
    private String code;

    private String msg;

    private T data;

    public ResDTO() {
    }

    public ResDTO(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public static <T> ResDTO<T> success(String msg, T data) {
        return new ResDTO<>("200",msg, data);
    }

    public static ResDTO success(String msg) {
        return new ResDTO("200",msg, null);
    }

    public static <T> ResDTO<T> success(T data) {
        return new ResDTO<>("200", StringUtils.EMPTY,data);
    }

    public static <T> ResDTO<T> fail(String msg, T data) {
        return new ResDTO<>("400",msg, data);
    }

    public static <T> ResDTO<T> fail(T data) {
        return new ResDTO<>("400",StringUtils.EMPTY, data);
    }

    public static ResDTO fail(String msg) {
        return new ResDTO("400",msg, null);
    }
}
