package com.hf.healthfriend.domain.like.exception;

import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

import static com.hf.healthfriend.domain.like.exception.LikeErrorCode.DUPLICATE_LIKE_ATTEMPTION;
import static com.hf.healthfriend.domain.like.exception.LikeErrorCode.LIKE_NOT_EXISTS;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.like")
public class LikeErrorControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BasicErrorResponse> noSuchElementException(NoSuchElementException e) {
        log.error("No Like exists", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(LIKE_NOT_EXISTS.status())
                        .statusCodeSeries(4)
                        .errorCode(LIKE_NOT_EXISTS.code())
                        .errorName(LIKE_NOT_EXISTS.name())
                        .message(LIKE_NOT_EXISTS.message())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateLikeException.class)
    public ResponseEntity<BasicErrorResponse> duplicateLikeException(DuplicateLikeException e) {
        log.error("Duplicate like attemption to Post={} by Member{}", e.getPostId(), e.getMemberId());
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(DUPLICATE_LIKE_ATTEMPTION.status())
                        .statusCodeSeries(4)
                        .errorName(DUPLICATE_LIKE_ATTEMPTION.name())
                        .errorCode(DUPLICATE_LIKE_ATTEMPTION.code())
                        .message(DUPLICATE_LIKE_ATTEMPTION.message())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
