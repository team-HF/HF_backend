package com.hf.healthfriend.auth.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum CookieConstants {
    COOKIE_DURATION_IN_SECONDS("300000");

    private final String value;

    public String getString() {
        return this.value;
    }

    public int getInt() throws NumberFormatException {
        return Integer.parseInt(this.value);
    }
}
