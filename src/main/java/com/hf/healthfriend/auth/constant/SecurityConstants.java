package com.hf.healthfriend.auth.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum SecurityConstants {
    AUTH_SERVER_COOKIE_KEY("token_auth_server"),
    REFRESH_TOKEN_COOKIE_KEY("refresh_token");

    private final String value;

    public String get() {
        return this.value;
    }
}
