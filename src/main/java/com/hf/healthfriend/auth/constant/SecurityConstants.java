package com.hf.healthfriend.auth.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum SecurityConstants {
    AUTH_SERVER_COOKIE_KEY("token_auth_server");

    private final String value;

    public String get() {
        return this.value;
    }
}
