package com.hf.healthfriend.domain.coupon.exception;

public class InvalidMatchingCountException extends InvalidRewardException {

    public InvalidMatchingCountException(String message) {
        super(message);
    }

    public InvalidMatchingCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMatchingCountException(Throwable cause) {
        super(cause);
    }
}
