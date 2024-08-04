package com.hf.healthfriend.global.spec;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiErrorResponse {
    private boolean status;
    private int code;
    private String message;

    @Builder
    public ApiErrorResponse(boolean status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}