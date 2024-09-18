package com.hf.healthfriend.domain.comment.controller.exceptionhandler;

import com.hf.healthfriend.global.spec.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.comment")
public class CommentExceptionHandlerControllerAdvice {

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiErrorResponse> dataIntegrityViolationException(DataIntegrityViolationException e) {
//        log.error("DataIntegrityViolationException occurred", e);
//        return ResponseEntity.badRequest()
//                .body(ApiErrorResponse.builder()
//                        .errorCode());
//    }
}
