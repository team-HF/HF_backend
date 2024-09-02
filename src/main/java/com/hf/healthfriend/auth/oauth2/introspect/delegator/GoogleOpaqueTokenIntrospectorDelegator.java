package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public class GoogleOpaqueTokenIntrospectorDelegator implements OpaqueTokenIntrospectorDelegator {
    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.GOOGLE;
    }
}
