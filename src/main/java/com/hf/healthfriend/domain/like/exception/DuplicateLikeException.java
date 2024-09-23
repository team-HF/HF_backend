package com.hf.healthfriend.domain.like.exception;

public class DuplicateLikeException extends RuntimeException {

    public DuplicateLikeException() {
        super();
    }

    public DuplicateLikeException(String message) {
        super(message);
    }

    public DuplicateLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateLikeException(Throwable cause) {
        super(cause);
    }
}
