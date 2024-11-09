package com.hf.healthfriend.domain.matching.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MatchingErrorCode {
    MEMBER_NOT_FOUND(40401, "MAT001", "해당 회원이 존재하지 않습니다");

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
