package com.hf.healthfriend.domain.coupon.exception;

public abstract class InvalidRewardException extends RuntimeException {

    public InvalidRewardException(String message) {
        super(message);
    }

    public InvalidRewardException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRewardException(Throwable cause) {
        super(cause);
    }
}
