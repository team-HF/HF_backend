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
                BasicErrorResponse.builder()
                        .statusCode(MatchingErrorCode.MEMBER_NOT_FOUND.status())
                        .errorName(MatchingErrorCode.MEMBER_NOT_FOUND.name())
                        .message(MatchingErrorCode.MEMBER_NOT_FOUND.message())
                        .statusCodeSeries(4)
                        .errorCode(MatchingErrorCode.MEMBER_NOT_FOUND.code())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }
}
