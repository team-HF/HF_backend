package com.hf.healthfriend.auth.exceptionhandling;

import com.hf.healthfriend.auth.exception.base.CustomAuthenticationException;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationExceptionHandler {

    ResponseEntity<ApiErrorResponse> handle(CustomAuthenticationException e);

    Class<? extends CustomAuthenticationException> getSupportingException();
}
