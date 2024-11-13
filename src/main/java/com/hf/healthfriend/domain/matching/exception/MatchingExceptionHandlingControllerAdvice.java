package com.hf.healthfriend.domain.matching.exception;

import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.matching")
public class MatchingExceptionHandlingControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                from(MatchingErrorCode.MEMBER_NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(OutOfLimitMatchingRequestException.class)
    public ResponseEntity<BasicErrorResponse> outOfLimitMatchingRequestException(OutOfLimitMatchingRequestException e) {
        return new ResponseEntity<>(
                from(MatchingErrorCode.OUT_OF_LIMIT_MATCHING_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    private BasicErrorResponse from(MatchingErrorCode errorCode) {
        return BasicErrorResponse.builder()
                .statusCode(errorCode.status())
                .errorName(errorCode.name())
                .message(errorCode.message())
                .statusCodeSeries(4)
                .errorCode(errorCode.code())
                .build();
    }
}
