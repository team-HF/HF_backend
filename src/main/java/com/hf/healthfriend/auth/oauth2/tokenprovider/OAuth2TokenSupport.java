package com.hf.healthfriend.auth.oauth2.tokenprovider;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;

/**
 * 각 AuthorizationServer의 Opaque Token에 대한 행위를 정의한 interface
 */
public interface OAuth2TokenSupport {

    // TODO: 인증 실패 시 구체적인 예외 사용
    GrantedTokenInfo grantToken(String code, String redirectUri) throws RuntimeException;

    boolean supports(AuthServer authServer);
}
