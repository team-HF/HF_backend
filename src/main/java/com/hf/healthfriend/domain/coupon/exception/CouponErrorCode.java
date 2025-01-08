package com.hf.healthfriend.domain.coupon.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CouponErrorCode {
    INTERNAL_SERVER_ERROR(50001, "C002", "애플리케이션 로직에 문제가 생겼습니다")
    ;

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
