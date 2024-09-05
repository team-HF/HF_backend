package com.hf.healthfriend.auth.oauth2.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class TokenValidationInfo {
    private final String email;
}
