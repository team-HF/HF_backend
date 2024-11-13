package com.hf.healthfriend.domain.coupon.exception;

import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
public class CouponNotFoundException extends NoSuchElementException {
    private final Long couponId;

    public CouponNotFoundException(Long couponId) {
        this.couponId = couponId;
    }

    public CouponNotFoundException(String s, Throwable cause, Long couponId) {
        super(s, cause);
        this.couponId = couponId;
    }

    public CouponNotFoundException(Throwable cause, Long couponId) {
        super(cause);
        this.couponId = couponId;
    }

    public CouponNotFoundException(String s, Long couponId) {
        super(s);
        this.couponId = couponId;
    }
}
