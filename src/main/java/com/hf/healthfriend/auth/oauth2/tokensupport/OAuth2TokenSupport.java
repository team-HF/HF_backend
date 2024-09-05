package com.hf.healthfriend.auth.oauth2.tokensupport;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;

/**
 * 각 AuthorizationServer의 Opaque Token에 대한 행위를 정의한 interface
 */
public interface OAuth2TokenSupport {

    @Deprecated
    // TODO: 인증 실패 시 구체적인 예외 사용
    GrantedTokenInfo grantToken(String code, String redirectUri) throws RuntimeException;

    // TODO: 유효하지 않은 토큰일 경우 구체적인 예외 사용
    TokenValidationInfo validateToken(String token) throws RuntimeException;

    // TODO: 유효하지 않은 토큰일 경우 구체적인 예외 사용
    String requestEmail(String accessToekn) throws RuntimeException;

    // TODO: 유효하지 않은 토큰일 경우 구체적인 예외 사용
    String refreshToken(String refreshToken) throws RuntimeException;

    boolean supports(AuthServer authServer);
}
