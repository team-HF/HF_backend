package com.hf.healthfriend.domain.review.exception;

import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.review.exception.ReviewErrorCode.*;

@Slf4j
@RestControllerAdvice
public class ReviewExceptionHandlerControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(MEMBER_NOT_FOUND.status())
                        .statusCodeSeries(4)
                        .errorCode(MEMBER_NOT_FOUND.code())
                        .message(MEMBER_NOT_FOUND.message() + " - reviewerId=" + e.getMemberId())
                        .errorName(MEMBER_NOT_FOUND.name())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MatchingNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> matchingNotFoundException(MatchingNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCodeSeries(4)
                        .statusCode(MATCHING_NOT_FOUND.status())
                        .errorName(MATCHING_NOT_FOUND.name())
                        .errorCode(MATCHING_NOT_FOUND.code())
                        .message(MATCHING_NOT_FOUND.message() + "- matchingId=" + e.getMatchingId())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidEvaluationsException.class)
    public ResponseEntity<BasicErrorResponse> invalidEvaluationException(InvalidEvaluationsException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCodeSeries(4)
                        .statusCode(INVALID_EVALUATIONS.status())
                        .errorCode(INVALID_EVALUATIONS.code())
                        .message(INVALID_EVALUATIONS.message())
                        .errorName(INVALID_EVALUATIONS.name())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BasicErrorResponse> illegalStateException(IllegalStateException e) {
        log.info("리뷰 남기기 실패", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCodeSeries(4)
                        .statusCode(ILLEGAL_MATCHING_STATE.status())
                        .errorCode(ILLEGAL_MATCHING_STATE.code())
                        .errorName(ILLEGAL_MATCHING_STATE.name())
                        .message(ILLEGAL_MATCHING_STATE.message())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<BasicErrorResponse> duplicateReviewException(DuplicateReviewException e) {
        log.info("중복된 리뷰 등록", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCodeSeries(4)
                        .statusCode(DUPLICATE_REVIEW.status())
                        .errorName(DUPLICATE_REVIEW.name())
                        .errorCode(DUPLICATE_REVIEW.code())
                        .message(DUPLICATE_REVIEW.message())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ReviewBeforeMeetingException.class)
    public ResponseEntity<BasicErrorResponse> reviewBeforeMeetingException(ReviewBeforeMeetingException e) {
        log.info("미팅 전에 리뷰 남기기", e);
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCodeSeries(4)
                        .statusCode(REVIEW_BEFORE_MEETING.status())
                        .errorCode(REVIEW_BEFORE_MEETING.code())
                        .errorName(REVIEW_BEFORE_MEETING.name())
                        .message(REVIEW_BEFORE_MEETING.message())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
