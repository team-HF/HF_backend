package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public interface OpaqueTokenIntrospectorDelegator {

    OAuth2AuthenticatedPrincipal introspect(String token);

    boolean supports(AuthServer authServer);
}
