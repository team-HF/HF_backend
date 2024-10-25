package com.hf.healthfriend.domain.spec.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum SpecErrorCode {
    MEMBER_NOT_FOUND(40400, "SP001", "해당 회원이 존재하지 않습니다");

    private int status;
    private String code;
    private String message;

    public int status() {
        return this.status;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
