package com.hf.healthfriend.domain.coupon.exception.handler;

import com.hf.healthfriend.domain.coupon.exception.CouponErrorCode;
import com.hf.healthfriend.domain.member.exception.errorcode.MemberErrorCode;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.coupon")
@Slf4j
public class CouponExceptionHandlingControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BasicErrorResponse> runtimeException(RuntimeException e) {
        // TODO: 왜 RuntimeException을 여기서 처리하기로 했는지 알아보고 전역 예외 처리에서 처리하는 곳으로 옮길 수 있도록

        log.error("에러 발생", e);
        return generateResponse(CouponErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        log.info("MemberNotFound", e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(MemberErrorCode.MEMBER_NOT_FOUND, Map.of("memberId", e.getMemberId())),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BasicErrorResponse> dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.info("DataIntegrityViolation", e);

        return new ResponseEntity<>(
                BasicErrorResponse.of(MemberErrorCode.MEMBER_NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }

    private ResponseEntity<BasicErrorResponse> generateResponse(CouponErrorCode errorCode, HttpStatus status) {
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .errorName(errorCode.name())
                        .errorCode(errorCode.code())
                        .message(errorCode.message())
                        .statusCodeSeries(status.series().value())
                        .statusCode(errorCode.status())
                        .build(),
                status
        );
    }
}
