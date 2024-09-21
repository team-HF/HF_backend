package com.hf.healthfriend.domain.post.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostErrorResponse {
    private String message;
    private String code;
    private int statusCode;
    private String detail;

    public PostErrorResponse(PostErrorCode code) {
        this.message = code.getMessage();
        this.statusCode = code.getStatus();
        this.code = code.getCode();
        this.detail = code.getDetail();
    }

    public static PostErrorResponse of(PostErrorCode code) {
        return new PostErrorResponse(code);
    }
}
