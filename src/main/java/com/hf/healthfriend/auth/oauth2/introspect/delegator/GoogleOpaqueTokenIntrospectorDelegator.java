package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import com.hf.healthfriend.auth.oauth2.principal.SingleAuthorityOAuth2Principal;
import com.hf.healthfriend.auth.oauth2.tokensupport.GoogleOAuth2TokenSupport;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class GoogleOpaqueTokenIntrospectorDelegator implements OpaqueTokenIntrospectorDelegator {
    private final MemberRepository memberRepository;
    private final GoogleOAuth2TokenSupport tokenSupport;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        TokenValidationInfo tokenValidationInfo = this.tokenSupport.validateToken(token);
        Member findMember = this.memberRepository.findById(tokenValidationInfo.getEmail())
                .orElseThrow(NoSuchElementException::new);
        return new SingleAuthorityOAuth2Principal(
                findMember.getId(),
                Map.of(
                        OAuth2TokenIntrospectionClaimNames.IAT, tokenValidationInfo.getIssuedAt(),
                        OAuth2TokenIntrospectionClaimNames.EXP, tokenValidationInfo.getExpiredAt()
                ),
                findMember.getAuthority()
        );
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.GOOGLE;
    }

    @Override
    public AuthServer getSupportingAuthServer() {
        return AuthServer.GOOGLE;
    }
}
