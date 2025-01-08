package com.hf.healthfriend.domain.comment.exception;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class CommentException extends RuntimeException implements Supplier<CommentException> {
    private CommentErrorCode errorCode;
    private HttpStatus httpStatus;
    private String details;

    public CommentException(CommentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    @Override
    public CommentException get() {
        return new CommentException(errorCode, httpStatus, details);
    }
}
