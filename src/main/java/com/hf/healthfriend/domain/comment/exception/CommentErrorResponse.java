package com.hf.healthfriend.domain.comment.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentErrorResponse {
    private String errorCode;
    private String message;
    private int statusCode;
    private String detail;

    public CommentErrorResponse(CommentErrorCode code, String detail) {
        this.errorCode = code.errorCode();
        this.message = code.message();
        this.statusCode = code.statusCode();
        this.detail = detail;
    }

    public static CommentErrorResponse of(CommentErrorCode code, String detail) {
        return new CommentErrorResponse(code,detail);
    }

    public static CommentErrorResponse of(CommentException exception) {
        return new CommentErrorResponse(
                exception.getErrorCode(),
                exception.getDetails()
        );
    }
}
