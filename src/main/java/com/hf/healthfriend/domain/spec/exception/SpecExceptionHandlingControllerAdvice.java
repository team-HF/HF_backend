package com.hf.healthfriend.domain.spec.exception;

import com.hf.healthfriend.domain.member.exception.errorcode.MemberErrorCode;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.spec")
public class SpecExceptionHandlingControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.of(MemberErrorCode.MEMBER_NOT_FOUND, Map.of("memberId", e.getMemberId())),
                HttpStatus.NOT_FOUND
        );
    }
}
