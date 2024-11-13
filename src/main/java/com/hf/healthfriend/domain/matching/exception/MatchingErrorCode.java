package com.hf.healthfriend.domain.matching.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MatchingErrorCode {
    MEMBER_NOT_FOUND(40401, "MAT001", "해당 회원이 존재하지 않습니다"),
    OUT_OF_LIMIT_MATCHING_REQUEST(40001, "MAT002", "중복된 매칭이 있습니다"),
    INVALID_COUPON_USAGE(40002, "MAT003", "사용할 수 없는 쿠폰입니다"),
    COUPON_NOT_FOUND(40402, "MAT004", "쿠폰이 존재하지 않습니다");

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
