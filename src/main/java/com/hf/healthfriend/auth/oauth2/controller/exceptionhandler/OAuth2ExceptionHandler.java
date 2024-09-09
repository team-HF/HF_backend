package com.hf.healthfriend.auth.oauth2.controller.exceptionhandler;

import com.hf.healthfriend.auth.exception.InvalidCodeException;
import com.hf.healthfriend.auth.exception.InvalidRefreshTokenException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hf.healthfriend.global.exception.ErrorCode.INVALID_CODE;
import static com.hf.healthfriend.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Slf4j
@RestControllerAdvice
public class OAuth2ExceptionHandler {

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<ApiErrorResponse> invalidCodeException(InvalidCodeException e) {
        log.error("""
                InvalidCodeException
                Error Code: {}
                HTTP Status: {}
                message: {}
                """,
                INVALID_CODE.code(),
                HttpStatus.UNAUTHORIZED.value(),
                INVALID_CODE.message(),
                e);

        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .statusCodeSeries(HttpStatus.UNAUTHORIZED.series().value())
                        .errorCode(INVALID_CODE.code())
                        .errorName(INVALID_CODE.name())
                        .message(INVALID_CODE.message())
                        .build(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiErrorResponse> invalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.error("""
                InvalidRefreshTokenException
                Error Code: {}
                HTTP Status: {}
                message: {}
                """,
                INVALID_REFRESH_TOKEN.code(),
                HttpStatus.UNAUTHORIZED.value(),
                INVALID_REFRESH_TOKEN.message(),
                e);

        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .statusCodeSeries(HttpStatus.UNAUTHORIZED.series().value())
                        .errorCode(INVALID_REFRESH_TOKEN.code())
                        .errorName(INVALID_REFRESH_TOKEN.name())
                        .message(INVALID_REFRESH_TOKEN.message())
                        .build(),
                HttpStatus.UNAUTHORIZED
        );
    }
}
