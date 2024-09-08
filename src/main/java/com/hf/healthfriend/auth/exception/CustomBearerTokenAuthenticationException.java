package com.hf.healthfriend.auth.exception;

import com.hf.healthfriend.auth.exception.base.CustomAuthenticationException;

public class CustomBearerTokenAuthenticationException extends CustomAuthenticationException {

    public CustomBearerTokenAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CustomBearerTokenAuthenticationException(String msg) {
        super(msg);
    }
}
