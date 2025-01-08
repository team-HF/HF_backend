package com.hf.healthfriend.domain.review.exception.handler;

import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.review.exception.DuplicateReviewException;
import com.hf.healthfriend.domain.review.exception.InvalidEvaluationsException;
import com.hf.healthfriend.domain.review.exception.ReviewBeforeMeetingException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.hf.healthfriend.domain.matching.exception.errorcode.MatchingErrorCode.MATCHING_NOT_FOUND;
import static com.hf.healthfriend.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.hf.healthfriend.domain.review.exception.errorcode.ReviewErrorCode.*;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.review")
public class ReviewExceptionHandlerControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.of(MEMBER_NOT_FOUND, Map.of("reviewerId", e.getMemberId())),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MatchingNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> matchingNotFoundException(MatchingNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.of(MATCHING_NOT_FOUND, Map.of("matchingId", e.getMatchingId())),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidEvaluationsException.class)
    public ResponseEntity<BasicErrorResponse> invalidEvaluationException(InvalidEvaluationsException e) {
        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(INVALID_EVALUATIONS));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BasicErrorResponse> illegalStateException(IllegalStateException e) {
        log.info("리뷰 남기기 실패", e);
        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(ILLEGAL_MATCHING_STATE));
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<BasicErrorResponse> duplicateReviewException(DuplicateReviewException e) {
        log.info("중복된 리뷰 등록", e);
        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(DUPLICATE_REVIEW));
    }

    @ExceptionHandler(ReviewBeforeMeetingException.class)
    public ResponseEntity<BasicErrorResponse> reviewBeforeMeetingException(ReviewBeforeMeetingException e) {
        log.info("미팅 전에 리뷰 남기기", e);
        return ResponseEntity.badRequest()
                .body(BasicErrorResponse.of(REVIEW_BEFORE_MEETING));
    }
}
