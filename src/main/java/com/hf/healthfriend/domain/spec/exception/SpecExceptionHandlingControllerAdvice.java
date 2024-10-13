package com.hf.healthfriend.domain.spec.exception;

import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.spec.exception.SpecErrorCode.MEMBER_NOT_FOUND;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.spec")
public class SpecExceptionHandlingControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .errorCode(MEMBER_NOT_FOUND.code())
                        .errorName(MEMBER_NOT_FOUND.name())
                        .statusCodeSeries(4)
                        .statusCode(MEMBER_NOT_FOUND.status())
                        .message(MEMBER_NOT_FOUND.message() + " - memberId=" + e.getMemberId())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
