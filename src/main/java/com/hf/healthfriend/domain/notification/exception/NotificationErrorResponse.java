package com.hf.healthfriend.domain.notification.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationErrorResponse {
    private String errorCode;
    private String message;
    private int statusCode;
    private String detail;

    public NotificationErrorResponse(NotificationErrorCode code, String detail) {
        this.errorCode = code.errorCode();
        this.message = code.message();
        this.statusCode = code.statusCode();
        this.detail = detail;
    }

    public static NotificationErrorResponse of(NotificationErrorCode code, String detail) {
        return new NotificationErrorResponse(code,detail);
    }

    public static NotificationErrorResponse of(NotificationException exception) {
        return new NotificationErrorResponse(
                exception.getErrorCode(),
                exception.getDetails()
        );
    }
}
