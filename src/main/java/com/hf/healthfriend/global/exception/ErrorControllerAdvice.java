package com.hf.healthfriend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain")
public class ErrorControllerAdvice {

    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<ErrorResponse> customException(CustomException e) {
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        response.setDetail(e.getMessage());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_REQUEST_FORMAT);
        response.setDetail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
