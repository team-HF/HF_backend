package com.hf.healthfriend.domain.comment.controller.exceptionhandler;

import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.comment.controller.exceptionhandler.CommentErrorCode.COMMENT_NOT_FOUND;
import static com.hf.healthfriend.domain.comment.controller.exceptionhandler.CommentErrorCode.POST_NOT_EXISTS;
import static com.hf.healthfriend.global.exception.ErrorCode.MEMBER_OF_THE_MEMBER_ID_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.comment")
public class CommentExceptionHandlerControllerAdvice {

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> dataIntegrityViolationException(CommentNotFoundException e) {
        log.error("CommentNotFoundException occurred", e);
        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .errorCode(COMMENT_NOT_FOUND.code())
                        .errorName(COMMENT_NOT_FOUND.name())
                        .message(COMMENT_NOT_FOUND.message() + ": " + e.getCommentId())
                        .statusCode(NOT_FOUND.value())
                        .statusCodeSeries(NOT_FOUND.series().value())
                        .build(),
                NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiErrorResponse> invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        log.error("InvalidDataAccessApiUsageException occurred", e);
        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        // TODO: post 도메인 쪽 ErrorCode를 활용하는 게 더 좋아 보인다.
                        .errorCode(POST_NOT_EXISTS.code())
                        .errorName(POST_NOT_EXISTS.name())
                        .message(POST_NOT_EXISTS.message())
                        .statusCode(NOT_FOUND.value())
                        .statusCodeSeries(NOT_FOUND.series().value())
                        .build(),
                NOT_FOUND
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> DataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException occurred", e);
        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .errorCode(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.code())
                        .errorName(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.name())
                        .message(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND.message())
                        .statusCode(NOT_FOUND.value())
                        .statusCodeSeries(NOT_FOUND.series().value())
                        .build(),
                NOT_FOUND
        );
    }
}
