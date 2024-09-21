package com.hf.healthfriend.domain.post.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.post")
public class PostErrorControllerAdvice {

    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<PostErrorResponse> customException(CustomException e) {
        PostErrorResponse response = PostErrorResponse.of(e.getErrorCode());
        response.setDetail(e.getMessage());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<PostErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        PostErrorResponse response = PostErrorResponse.of(PostErrorCode.INVALID_REQUEST_FORMAT);
        response.setDetail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
