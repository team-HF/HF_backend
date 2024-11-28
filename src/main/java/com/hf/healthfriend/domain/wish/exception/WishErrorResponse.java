package com.hf.healthfriend.domain.wish.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WishErrorResponse {
    private String errorCode;
    private String message;
    private int statusCode;
    private String detail;

    public WishErrorResponse(WishErrorCode code, String detail) {
        this.errorCode = code.errorCode();
        this.message = code.message();
        this.statusCode = code.statusCode();
        this.detail = detail;
    }

    public static WishErrorResponse of(WishErrorCode code, String detail) {
        return new WishErrorResponse(code,detail);
    }

    public static WishErrorResponse of(WishException exception) {
        return new WishErrorResponse(
                exception.getErrorCode(),
                exception.getDetails()
        );
    }
}
