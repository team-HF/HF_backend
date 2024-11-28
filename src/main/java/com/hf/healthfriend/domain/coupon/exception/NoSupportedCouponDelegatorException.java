package com.hf.healthfriend.domain.coupon.exception;

public class NoSupportedCouponDelegatorException extends RuntimeException {
    private final Class<?> notSupportedType;

    public NoSupportedCouponDelegatorException(Class<?> notSupportedType) {
        this.notSupportedType = notSupportedType;
    }

    public NoSupportedCouponDelegatorException(String message, Class<?> notSupportedType) {
        super(message);
        this.notSupportedType = notSupportedType;
    }

    public NoSupportedCouponDelegatorException(String message, Throwable cause, Class<?> notSupportedType) {
        super(message, cause);
        this.notSupportedType = notSupportedType;
    }

    public NoSupportedCouponDelegatorException(Throwable cause, Class<?> notSupportedType) {
        super(cause);
        this.notSupportedType = notSupportedType;
    }
}
