package com.hf.healthfriend.domain.coupon.exception;

public class AmbiguousDelegatorException extends RuntimeException {

    public AmbiguousDelegatorException(String message) {
        super(message);
    }

    public AmbiguousDelegatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousDelegatorException(Throwable cause) {
        super(cause);
    }
}
