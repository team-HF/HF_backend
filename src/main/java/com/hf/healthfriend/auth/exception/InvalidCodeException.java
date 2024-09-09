package com.hf.healthfriend.auth.exception;

/**
 * OAuth 2.0 Authorization Server로부터 Access Token을 받기 위한 인가 코드가 유효하지 않을 경우 던져지는 Exception
 */
public class InvalidCodeException extends RuntimeException {

    public InvalidCodeException(String message) {
        super(message);
    }

    public InvalidCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
