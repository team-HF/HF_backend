package com.hf.healthfriend.global.exception.custom;

import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException{

    private CommonErrorCode errorCode;

    public MemberException(CommonErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
