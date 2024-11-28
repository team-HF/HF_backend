package com.hf.healthfriend.domain.comment.exception.handler;

import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.exception.PostNotFoundException;
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

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> dataIntegrityViolationException(CommentNotFoundException e) {
        log.error("CommentNotFoundException occurred", e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(COMMENT_NOT_FOUND, Map.of("commentId", e.getCommentId())),
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
    public ResponseEntity<BasicErrorResponse> DataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException occurred", e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(MEMBER_NOT_FOUND),
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
                BasicErrorResponse.of(MEMBER_NOT_FOUND, Map.of("writerId", e.getMemberId())),
                NOT_FOUND
        );
    }
}
