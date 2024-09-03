package com.hf.healthfriend.auth.oauth2.introspect;

import com.hf.healthfriend.auth.oauth2.introspect.delegator.OpaqueTokenIntrospectorDelegator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Auth Server에 따라 적합한 Introspector에게 맡김
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelegatingOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final List<OpaqueTokenIntrospectorDelegator> delegators;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        log.debug("Delegators={}", this.delegators);
        for (OpaqueTokenIntrospectorDelegator delegator : this.delegators) {
            log.debug("Auth Server: {}", delegator.getSupportingAuthServer());
            try {
                return delegator.introspect(token);
            } catch (NoSuchElementException e) {
                // Token의 Owner가 우리 서비스에 등록되지 않을 경우 (DB에 없을 경우) 발생
                // TODO: 나중에 NoSuchElementException이 아닌 다른 Exception을 사용할 수 있다.
                log.warn("등록되지 않은 사용자");
            } catch (Exception e) { // TODO: 더 세밀한 예외 처리 필요 (아마?)
                log.warn("{}는 지원하지 않음", delegator.getSupportingAuthServer(), e);
            }
        }
        OAuth2Error oAuth2Error = new OAuth2Error("-1"); // TODO: Error Code 정의해야 함
        throw new OAuth2AuthenticationException(oAuth2Error, "Invalid Token: " + token);
    }
}
