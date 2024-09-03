package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public interface OpaqueTokenIntrospectorDelegator {

    // TODO: 각 Exception Case에 따라 세밀한 Exception을 던져야 한다.
    // 1. 유효하지 않은 토큰일 경우
    // 2. 토큰은 유효하나 해당 유저가 DB에 없을 경우
    OAuth2AuthenticatedPrincipal introspect(String token) throws RuntimeException;

    boolean supports(AuthServer authServer);

    AuthServer getSupportingAuthServer();
}
