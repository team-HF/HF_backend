package com.hf.healthfriend.global.util;

import com.hf.healthfriend.auth.constant.CookieConstants;
import org.springframework.http.ResponseCookie;

public class SecuredHttpCookieUtils implements HttpCookieUtils {
    private final String domain;

    public SecuredHttpCookieUtils(String domain) {
        this.domain = domain;
    }

    @Override
    public ResponseCookie buildResponseCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .maxAge(CookieConstants.COOKIE_DURATION_IN_SECONDS.getInt())
                .domain(this.domain)
                .path("/")
                .sameSite("None")
                .secure(true) // localhost 환경에서는 secure 쿠키도 전달됨
                .build();
    }
}
