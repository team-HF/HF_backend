package com.hf.healthfriend.domain.post.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostErrorResponse {
    private String errorCode;
    private String message;
    private int statusCode;
    private String detail;

    public PostErrorResponse(PostErrorCode code,String detail) {
        this.errorCode = code.errorCode();
        this.message = code.message();
        this.statusCode = code.statusCode();
        this.detail = detail;
    }

    public static PostErrorResponse of(PostErrorCode code, String detail) {
        return new PostErrorResponse(code,detail);
    }

    public static PostErrorResponse of(PostException exception) {
        return new PostErrorResponse(
                exception.getErrorCode(),
                exception.getDetails()
        );
    }
}
