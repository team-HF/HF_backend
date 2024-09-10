package com.hf.healthfriend.auth.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum CookieConstants {
    COOKIE_DURATION_IN_SECONDS("300000"),
    AUTH_SERVER_COOKIE_KEY("token_auth_server"), // 이건 아마 안 쓸 듯
    COOKIE_NAME_ACCESS_TOKEN("access_token"),
    COOKIE_NAME_EMAIL("email"),
    COOKIE_NAME_IS_NEW_MEMBER("is_new_member"),
    COOKIE_NAME_REFRESH_TOKEN("refresh_token");

    private final String value;

    public String getString() {
        return this.value;
    }

    public int getInt() throws NumberFormatException {
        return Integer.parseInt(this.value);
    }
}
