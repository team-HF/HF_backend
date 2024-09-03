package com.hf.healthfriend.auth.oauth2.introspect;

import com.hf.healthfriend.auth.constant.SecurityConstants;
import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.introspect.delegator.OpaqueTokenIntrospectorDelegator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auth Server에 따라 적합한 Introspector에게 맡김
 */
@Slf4j
@Component
public class DelegatingOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private static final String DELIMITER = SecurityConstants.REQUEST_BEARER_TOKEN_DELIMITER.get();

    private final Map<AuthServer, OpaqueTokenIntrospectorDelegator> delegatorsByAuthServer;

    public DelegatingOpaqueTokenIntrospector(List<OpaqueTokenIntrospectorDelegator> delegators) {
        this.delegatorsByAuthServer = new HashMap<>();
        for (AuthServer authServer : AuthServer.values()) {
            for (OpaqueTokenIntrospectorDelegator delegator : delegators) {
                if (delegator.supports(authServer)) {
                    this.delegatorsByAuthServer.put(authServer, delegator);
                }
            }
        }
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        String[] split = token.split(DELIMITER);

        // TODO: IllegalArgumentException 발생 -> 지원하지 않는 Token이거나 요청이 잘못된 것을 의미
        AuthServer authServer = AuthServer.valueOf(split[0]);
        String accessToken = split[1];

        if (log.isTraceEnabled()) {
            log.trace("Auth Server={}", authServer);
            log.trace("Token value={}", accessToken);
        }

        OpaqueTokenIntrospectorDelegator delegator = this.delegatorsByAuthServer.get(authServer);
        return delegator.introspect(accessToken);
    }
}
