package com.hf.healthfriend.domain.coupon.exception;

import lombok.Getter;

@Getter
public class UnavailableCouponException extends RuntimeException {
    private final Long couponId;

    public UnavailableCouponException(Long couponId) {
        this.couponId = couponId;
    }

    public UnavailableCouponException(String message, Long couponId) {
        super(message);
        this.couponId = couponId;
    }

    public UnavailableCouponException(String message, Throwable cause, Long couponId) {
        super(message, cause);
        this.couponId = couponId;
    }

    public UnavailableCouponException(Throwable cause, Long couponId) {
        super(cause);
        this.couponId = couponId;
    }
}
