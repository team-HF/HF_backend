package com.hf.healthfriend.domain.matching.exception;

public class OutOfLimitMatchingRequestException extends RuntimeException {

    public OutOfLimitMatchingRequestException(String message) {
        super(message);
    }

    public OutOfLimitMatchingRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfLimitMatchingRequestException(Throwable cause) {
        super(cause);
    }
}
