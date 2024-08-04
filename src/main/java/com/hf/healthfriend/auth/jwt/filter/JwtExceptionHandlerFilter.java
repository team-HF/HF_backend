package com.hf.healthfriend.auth.jwt.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.global.exception.custom.CustomJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {
            handleException(response, e.getCommonErrorCode().getHttpStatus(), e.getCommonErrorCode().getMessage());
        } catch (UsernameNotFoundException e) {
            handleException(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private void handleException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("status", false);
        jsonResponse.put("message", message);
        jsonResponse.put("code", status.value());

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
