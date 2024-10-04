package com.hf.healthfriend.domain.member.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum MemberErrorCode {
    MEMBER_CREATION_VALIDATION_ERROR(40001, "M001", "회원 등록 에러");

    private final int status;
    private final String code;
    private final String message;

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
