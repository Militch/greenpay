package com.esiran.greenadmin.common.entity;

import lombok.Data;

@Data
public class APIError {
    public APIError() {
    }

    public APIError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;
}
