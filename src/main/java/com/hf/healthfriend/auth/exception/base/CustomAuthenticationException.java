package com.hf.healthfriend.auth.exception.base;

public abstract class CustomAuthenticationException extends RuntimeException {

    public CustomAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CustomAuthenticationException(String msg) {
        super(msg);
    }
}
