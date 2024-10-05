package com.hf.healthfriend.global.util.mapping.exception;

public class InvalidTargetNameException extends MappingException {

    public InvalidTargetNameException(String message) {
        super(message);
    }

    public InvalidTargetNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTargetNameException(Throwable cause) {
        super(cause);
    }
}
