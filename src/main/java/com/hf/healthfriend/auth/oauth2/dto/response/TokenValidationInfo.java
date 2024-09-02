package com.hf.healthfriend.auth.oauth2.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
@ToString
public class TokenValidationInfo {
    private final String email;
    private final Instant issuedAt;
    private final Instant expiredAt;
}
