package com.hf.healthfriend.auth.oauth2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TokenRefreshResponseDto {
    private final String accessToken;
}
