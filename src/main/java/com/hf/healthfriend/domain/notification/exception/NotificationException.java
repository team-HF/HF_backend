package com.hf.healthfriend.domain.notification.exception;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class NotificationException extends RuntimeException implements Supplier<NotificationException> {
    private NotificationErrorCode errorCode;
    private HttpStatus httpStatus;
    private String details;

    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    @Override
    public NotificationException get() {
        return new NotificationException(errorCode, httpStatus, details);
    }
}
