package com.hf.healthfriend.global.util.mapping.exception;

public class MappingNotExistsException extends MappingException {

    public MappingNotExistsException(String message) {
        super(message);
    }

    public MappingNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingNotExistsException(Throwable cause) {
        super(cause);
    }
}
