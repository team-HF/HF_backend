package com.hf.healthfriend.domain.notification.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.post")
public class NotificationErrorControllerAdvice {
    @ExceptionHandler(value = NotificationException.class)
    protected ResponseEntity<NotificationErrorResponse> postException(NotificationException e) {
        NotificationErrorResponse response = NotificationErrorResponse.of(e.getErrorCode(),e.getDetails());
        response.setMessage(e.getMessage());
        response.setDetail(e.getDetails());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }
}
