package com.hf.healthfriend.auth.oauth2.introspect;

import com.hf.healthfriend.auth.exception.CustomBearerTokenAuthenticationException;
import com.hf.healthfriend.auth.oauth2.introspect.delegator.OpaqueTokenIntrospectorDelegator;
import com.hf.healthfriend.auth.oauth2.principal.SingleAuthorityOAuth2Principal;
import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Auth Server에 따라 적합한 Introspector에게 맡김
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!no-auth")
public class DelegatingOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final List<OpaqueTokenIntrospectorDelegator> delegators;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        log.debug("Delegators={}", this.delegators);
        for (OpaqueTokenIntrospectorDelegator delegator : this.delegators) {
            log.debug("Auth Server: {}", delegator.getSupportingAuthServer());
            try {
                return delegator.introspect(token);
            } catch (MemberNotFoundException e) {
                // Token의 Owner가 우리 서비스에 등록되지 않을 경우 (DB에 없을 경우) 발생
                log.warn("등록되지 않은 사용자: {}", e.getMemberId());
                return new SingleAuthorityOAuth2Principal(e.getMemberId(), Role.ROLE_NON_MEMBER);
            } catch (Exception e) { // TODO: 더 세밀한 예외 처리 필요 (아마?)
                log.warn("{}는 지원하지 않음", delegator.getSupportingAuthServer(), e);
            }
        }

        throw new CustomBearerTokenAuthenticationException("유효하지 않은 Access Token");
    }
}
