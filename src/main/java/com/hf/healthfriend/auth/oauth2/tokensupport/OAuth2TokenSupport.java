package com.hf.healthfriend.auth.oauth2.tokensupport;

import com.hf.healthfriend.auth.exception.InvalidCodeException;
import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;

/**
 * 각 AuthorizationServer의 Opaque Token에 대한 행위를 정의한 interface
 */
public interface OAuth2TokenSupport {

    /**
     * Redirection URI로 전송된 인가코드를 바탕으로 OAuth 2.0 Authorization Server로부터 Access Token과 Refresh Token,
     * 그리고 email과 같은 사용자 정보를 가져온다.
     * @param code 인가 코드
     * @param redirectUri Redirect URI
     * @return Access Token, Refresh Token, email이 정보가 담긴 인스턴스
     * @throws InvalidCodeException 인가 코드가 유효하지 않을 경우
     */
    GrantedTokenInfo grantToken(String code, String redirectUri) throws InvalidCodeException;

    // TODO: 유효하지 않은 토큰일 경우 구체적인 예외 사용
    TokenValidationInfo validateToken(String token) throws RuntimeException;

    // TODO: 유효하지 않은 토큰일 경우 구체적인 예외 사용
    String requestEmail(String accessToekn) throws RuntimeException;

    // TODO: 유효하지 않은 토큰일 경우 구체적인 예외 사용
    String refreshToken(String refreshToken) throws RuntimeException;

    boolean supports(AuthServer authServer);
}
