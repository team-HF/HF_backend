package com.hf.healthfriend.auth.oauth2.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class WrappingRefreshTokenWithCookieRequestDto {
    private String refreshToken;
}
