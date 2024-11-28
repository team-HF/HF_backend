package com.hf.healthfriend.domain.like.exception.handler;

import com.hf.healthfriend.domain.like.exception.DuplicatePostLikeException;
import com.hf.healthfriend.domain.like.exception.PostOrMemberNotExistsException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

import static com.hf.healthfriend.domain.like.exception.errorcode.LikeErrorCode.*;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.like")
public class LikeErrorControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BasicErrorResponse> noSuchElementException(NoSuchElementException e) {
        log.info("No Like exists", e);
        return new ResponseEntity<>(
                BasicErrorResponse.of(LIKE_NOT_EXISTS),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicatePostLikeException.class)
    public ResponseEntity<BasicErrorResponse> duplicateLikeException(DuplicatePostLikeException e) {
        log.info("Duplicate like attemption to Post={} by Member{}", e.getPostId(), e.getMemberId());
        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(DUPLICATE_LIKE_ATTEMPTION,
                        Map.of("memberId", e.getMemberId(), "postId", e.getPostId())));
    }

    @ExceptionHandler(PostOrMemberNotExistsException.class)
    public ResponseEntity<BasicErrorResponse> postOfMemberNotExistsException(PostOrMemberNotExistsException e) {
        log.error("Like ## either memberId={} or postId={} not exists", e.getPostId(), e.getMemberId());
        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(POST_OR_MEMBER_NOT_EXISTS,
                        Map.of("memberId", e.getMemberId(), "postId", e.getPostId())));
    }
}
