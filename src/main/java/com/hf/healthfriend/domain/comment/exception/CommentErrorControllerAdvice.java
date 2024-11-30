package com.hf.healthfriend.domain.comment.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.post")
public class CommentErrorControllerAdvice {
    @ExceptionHandler(value = CommentException.class)
    protected ResponseEntity<CommentErrorResponse> postException(CommentException e) {
        CommentErrorResponse response = CommentErrorResponse.of(e.getErrorCode(),e.getDetails());
        response.setMessage(e.getMessage());
        response.setDetail(e.getDetails());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<CommentErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        CommentErrorResponse response = CommentErrorResponse.of(CommentErrorCode.INVALID_COMMENT_REQUEST_FORMAT, null);
        response.setDetail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
