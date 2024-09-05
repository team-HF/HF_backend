package com.hf.healthfriend.global.util;

import com.hf.healthfriend.auth.constant.CookieConstants;
import org.springframework.http.ResponseCookie;

public class NoSecurityHttpCookieUtils implements HttpCookieUtils {
    private final String domain;

    public NoSecurityHttpCookieUtils(String domain) {
        this.domain = domain;
    }

    @Override
    public ResponseCookie buildResponseCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .maxAge(CookieConstants.COOKIE_DURATION_IN_SECONDS.getInt())
                .domain(this.domain)
                .path("/")
                .build();
    }
}
