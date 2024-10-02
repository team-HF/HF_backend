package com.hf.healthfriend.domain.like.exception;

import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

import static com.hf.healthfriend.domain.like.exception.LikeErrorCode.*;

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

    @ExceptionHandler(DuplicatePostLikeException.class)
    public ResponseEntity<BasicErrorResponse> duplicateLikeException(DuplicatePostLikeException e) {
        log.error("Duplicate like attemption to Post={} by Member{}", e.getPostId(), e.getMemberId());
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(DUPLICATE_LIKE_ATTEMPTION.status())
                        .statusCodeSeries(4)
                        .errorName(DUPLICATE_LIKE_ATTEMPTION.name())
                        .errorCode(DUPLICATE_LIKE_ATTEMPTION.code())
                        .message(String.format(DUPLICATE_LIKE_ATTEMPTION.message(), e.getMemberId(), e.getPostId()))
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PostOrMemberNotExistsException.class)
    public ResponseEntity<BasicErrorResponse> postOfMemberNotExistsException(PostOrMemberNotExistsException e) {
        log.error("Post insertions ## memberId={}, postId={} not exists", e.getPostId(), e.getMemberId());
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(POST_OR_MEMBER_NOT_EXISTS.status())
                        .statusCodeSeries(4)
                        .errorName(POST_OR_MEMBER_NOT_EXISTS.name())
                        .errorCode(POST_OR_MEMBER_NOT_EXISTS.code())
                        .message(String.format(POST_OR_MEMBER_NOT_EXISTS.message(), e.getMemberId(), e.getPostId()))
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
