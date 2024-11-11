package com.hf.healthfriend.domain.coupon.exception;

import lombok.Getter;

@Getter
public class AlreadyUsedCouponException extends RuntimeException {
    private Long couponId;

    public AlreadyUsedCouponException(Long couponId) {
        this.couponId = couponId;
    }

    public AlreadyUsedCouponException(String message, Long couponId) {
        super(message);
        this.couponId = couponId;
    }

    public AlreadyUsedCouponException(String message, Throwable cause, Long couponId) {
        super(message, cause);
        this.couponId = couponId;
    }

    public AlreadyUsedCouponException(Throwable cause, Long couponId) {
        super(cause);
        this.couponId = couponId;
    }
}
