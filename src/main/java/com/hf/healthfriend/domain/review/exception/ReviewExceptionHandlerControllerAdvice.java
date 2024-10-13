package com.hf.healthfriend.domain.review.exception;

import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.review.exception.ReviewErrorCode.MATCHING_NOT_FOUND;
import static com.hf.healthfriend.domain.review.exception.ReviewErrorCode.MEMBER_NOT_FOUND;

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
}
