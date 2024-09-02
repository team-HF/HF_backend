package com.hf.healthfriend.auth.oauth2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@ToString
public class GrantedTokenInfo {
    private final String accessToken;
    private final String refreshToken;
    private final LocalDateTime expirationTime;
    private final String email;
}
