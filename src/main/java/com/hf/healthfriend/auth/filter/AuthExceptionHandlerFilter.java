package com.hf.healthfriend.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.auth.exception.base.CustomAuthenticationException;
import com.hf.healthfriend.auth.exceptionhandling.AuthenticationExceptionHandler;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AuthExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final Map<Class<? extends CustomAuthenticationException>, AuthenticationExceptionHandler> handlers;

    public AuthExceptionHandlerFilter(ObjectMapper objectMapper, List<AuthenticationExceptionHandler> exceptionHandlers) {
        this.objectMapper = objectMapper;
        this.handlers = new HashMap<>();
        for (AuthenticationExceptionHandler handler : exceptionHandlers) {
            this.handlers.put(handler.getSupportingException(), handler);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomAuthenticationException e) {
            log.debug("Class of Exception: {}", e.getClass());

            AuthenticationExceptionHandler handler = this.handlers.get(e.getClass());

            if (handler == null) {
                log.warn("{}에 대응되는 Exception Handler가 없습니다.", e.getClass());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            ResponseEntity<ApiErrorResponse> responseEntity = handler.handle(e);

            response.setStatus(responseEntity.getStatusCode().value());
            responseEntity.getHeaders()
                    .forEach((k, v) -> v.forEach((value) -> response.addHeader(k, value)));
            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            try (BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream())) {
                os.write(this.objectMapper.writeValueAsBytes(responseEntity.getBody()));
            }
        }
    }
}
