package com.hf.healthfriend.auth.oauth2.controller.exceptionhandler;

import com.hf.healthfriend.auth.exception.InvalidCodeException;
import com.hf.healthfriend.auth.exception.InvalidRefreshTokenException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

import static com.hf.healthfriend.global.exception.ErrorCode.INVALID_CODE;
import static com.hf.healthfriend.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Slf4j
@RestControllerAdvice
public class OAuth2ExceptionHandler {
    private final String clientOrigin;

    public OAuth2ExceptionHandler(@Value("${client.origin}") String clientOrigin) {
        this.clientOrigin = clientOrigin;
    }

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<Void> invalidCodeException(InvalidCodeException e) {
        log.error("""
                        InvalidCodeException
                        Error Code: {}
                        HTTP Status: {}
                        message: {}
                        """,
                INVALID_CODE.code(),
                HttpStatus.PERMANENT_REDIRECT.value(),
                INVALID_CODE.message(),
                e);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                UriComponentsBuilder.fromHttpUrl(this.clientOrigin)
                        .path("/") // TODO: 유효하지 않은 인가코드일 경우 보내질 경로 (프론트 경로 설정)
                        .queryParam("error-code", INVALID_CODE.code())
                        .queryParam("error-name", INVALID_CODE.name())
                        .queryParam("message", INVALID_CODE.message())
                        .build()
                        .toUri()
        );

        return new ResponseEntity<>(
                headers,
                HttpStatus.TEMPORARY_REDIRECT
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
