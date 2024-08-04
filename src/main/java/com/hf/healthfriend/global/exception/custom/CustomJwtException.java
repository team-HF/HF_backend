package com.hf.healthfriend.global.exception.custom;

import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import lombok.Getter;

@Getter
public class CustomJwtException extends RuntimeException{

    private CommonErrorCode commonErrorCode;

    public CustomJwtException(CommonErrorCode commonErrorCode) {
        this.commonErrorCode = commonErrorCode;
    }
}
