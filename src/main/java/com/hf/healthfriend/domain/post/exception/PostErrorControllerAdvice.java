package com.hf.healthfriend.domain.post.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.post")
public class PostErrorControllerAdvice {
    @ExceptionHandler(value = PostException.class)
    protected ResponseEntity<PostErrorResponse> postException(PostException e) {
        PostErrorResponse response = PostErrorResponse.of(e.getErrorCode(),e.getDetails());
        response.setMessage(e.getMessage());
        response.setDetail(e.getDetails());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<PostErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        PostErrorResponse response = PostErrorResponse.of(PostErrorCode.INVALID_POST_REQUEST_FORMAT, null);
        response.setDetail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
