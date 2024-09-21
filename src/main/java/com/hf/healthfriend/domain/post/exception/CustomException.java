package com.hf.healthfriend.domain.post.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException implements Supplier<CustomException> {
    private PostErrorCode errorCode;
    private HttpStatus httpStatus;

    public CustomException(PostErrorCode errorCode){
        super(errorCode.getMessage());
    }

    @Override
    public CustomException get() {
        return new CustomException(errorCode, httpStatus);
    }
}
