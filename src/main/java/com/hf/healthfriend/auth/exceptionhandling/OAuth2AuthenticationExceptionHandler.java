package com.hf.healthfriend.auth.exceptionhandling;

import com.hf.healthfriend.auth.exception.CustomBearerTokenAuthenticationException;
import com.hf.healthfriend.auth.exception.base.CustomAuthenticationException;
import com.hf.healthfriend.global.exception.ErrorCode;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationExceptionHandler implements AuthenticationExceptionHandler {

    @Override
    public ResponseEntity<ApiErrorResponse> handle(CustomAuthenticationException e) {
        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .statusCodeSeries(HttpStatus.UNAUTHORIZED.series().value())
                        .errorCode(ErrorCode.UNAUTHENTICATED.code())
                        .errorName(ErrorCode.UNAUTHENTICATED.name())
                        .message(ErrorCode.UNAUTHENTICATED.message())
                        .build(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @Override
    public Class<? extends CustomAuthenticationException> getSupportingException() {
        return CustomBearerTokenAuthenticationException.class;
    }
}
