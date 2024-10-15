package com.hf.healthfriend.domain.comment.controller.exceptionhandler;

import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.exception.PostNotFoundException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.comment.controller.exceptionhandler.CommentErrorCode.*;
import static com.hf.healthfriend.global.exception.ErrorCode.MEMBER_OF_THE_MEMBER_ID_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.comment")
public class CommentExceptionHandlerControllerAdvice {

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> dataIntegrityViolationException(CommentNotFoundException e) {
        log.error("CommentNotFoundException occurred", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
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
    public ResponseEntity<BasicErrorResponse> invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        log.error("InvalidDataAccessApiUsageException occurred", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
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

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> postNotFoundException(PostNotFoundException e) {
        log.error("Post not found exception", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCodeSeries(4)
                        .statusCode(POST_NOT_EXISTS.status())
                        .errorCode(POST_NOT_EXISTS.code())
                        .message(POST_NOT_EXISTS.message() + " - postId=" + e.getPostId())
                        .errorName(POST_NOT_EXISTS.name())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFonudException(MemberNotFoundException e) {
        log.error("Member not found", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(MEMBER_NOT_EXISTS.status())
                        .statusCodeSeries(4)
                        .errorName(MEMBER_NOT_EXISTS.code())
                        .errorCode(MEMBER_NOT_EXISTS.code())
                        .message(MEMBER_NOT_EXISTS.message() + " - writer=" + e.getMemberId())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
