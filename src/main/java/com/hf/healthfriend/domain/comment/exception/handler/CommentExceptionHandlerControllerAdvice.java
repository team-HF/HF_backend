package com.hf.healthfriend.domain.comment.exception.handler;

import com.hf.healthfriend.domain.comment.exception.CommentErrorCode;
import com.hf.healthfriend.domain.comment.exception.CommentException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.hf.healthfriend.domain.comment.exception.errorcode.CommentErrorCode.*;
import static com.hf.healthfriend.domain.member.exception.errorcode.MemberErrorCode.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.comment")
public class CommentExceptionHandlerControllerAdvice {

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<BasicErrorResponse> commentException(CommentException e) {
        log.info("commentException", e);
        CommentErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity(
                BasicErrorResponse.builder()
                        .statusCode(errorCode.statusCode())
                        .statusCodeSeries(errorCode.statusCode() / 10000)
                        .errorCode(errorCode.errorCode())
                        .message(errorCode.message())
                        .errorName(errorCode.name())
                        .build(),
                e.getHttpStatus()
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
    public ResponseEntity<BasicErrorResponse> DataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException occurred", e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(MEMBER_NOT_FOUND),
                NOT_FOUND
        );
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFonudException(MemberNotFoundException e) {
        log.error("Member not found", e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(MEMBER_NOT_FOUND, Map.of("writerId", e.getMemberId())),
                NOT_FOUND
        );
    }
}
