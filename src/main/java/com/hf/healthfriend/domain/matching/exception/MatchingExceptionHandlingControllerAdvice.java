package com.hf.healthfriend.domain.matching.exception;

import com.hf.healthfriend.domain.coupon.exception.CouponNotFoundException;
import com.hf.healthfriend.domain.coupon.exception.UnavailableCouponException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.global.spec.BasicErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.domain.matching.exception.MatchingErrorCode.*;

@Slf4j
@RestControllerAdvice(basePackages = "com.hf.healthfriend.domain.matching")
public class MatchingExceptionHandlingControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return from(MEMBER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OutOfLimitMatchingRequestException.class)
    public ResponseEntity<BasicErrorResponse> outOfLimitMatchingRequestException(OutOfLimitMatchingRequestException e) {
        return from(OUT_OF_LIMIT_MATCHING_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<BasicErrorResponse> couponNotFoundException(CouponNotFoundException e) {
        log.info("존재하지 않는 쿠폰 사용 - couponId={}", e.getCouponId());
        return from(COUPON_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnavailableCouponException.class)
    public ResponseEntity<BasicErrorResponse> unavailableCouponException(UnavailableCouponException e) {
        log.info("유효하지 않은 쿠폰 사용 - couponId={}", e.getCouponId());
        return from(INVALID_COUPON_USAGE, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BasicErrorResponse> from(MatchingErrorCode errorCode, HttpStatus status) {
        return new ResponseEntity<>(
                BasicErrorResponse.builder()
                        .statusCode(errorCode.status())
                        .errorName(errorCode.name())
                        .message(errorCode.message())
                        .statusCodeSeries(4)
                        .errorCode(errorCode.code())
                        .build(),
                status
        );
    }
}
