package com.hf.healthfriend.domain.matching.exception.handler;

import com.hf.healthfriend.domain.matching.exception.errorcode.MatchingErrorCode;
import com.hf.healthfriend.domain.matching.exception.OutOfLimitMatchingRequestException;
import com.hf.healthfriend.domain.member.exception.errorcode.MemberErrorCode;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.matching")
public class MatchingExceptionHandlingControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.of(MemberErrorCode.MEMBER_NOT_FOUND, Map.of("memberId", e.getMemberId())),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(OutOfLimitMatchingRequestException.class)
    public ResponseEntity<BasicErrorResponse> outOfLimitMatchingRequestException(OutOfLimitMatchingRequestException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.of(MatchingErrorCode.OUT_OF_LIMIT_MATCHING_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }
}
