package com.hf.healthfriend.domain.post.exception;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class PostException extends RuntimeException implements Supplier<PostException> {
    private PostErrorCode errorCode;
    private HttpStatus httpStatus;
    private String details;

    public PostException(PostErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    @Override
    public PostException get() {
        return new PostException(errorCode, httpStatus, details);
    }
}
