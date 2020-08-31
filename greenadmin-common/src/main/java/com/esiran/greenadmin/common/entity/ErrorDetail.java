package com.esiran.greenadmin.common.entity;

import lombok.Data;

@Data
public class ErrorDetail {
    private String code;
    private String message;

    public ErrorDetail() {
    }

    public ErrorDetail(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
