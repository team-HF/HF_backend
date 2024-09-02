package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import com.hf.healthfriend.auth.oauth2.tokenprovider.OAuth2TokenSupport;
import com.hf.healthfriend.auth.principal.SingleAuthorityOAuth2Principal;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class KakaoOpaqueTokenIntrospectorDelegator implements OpaqueTokenIntrospectorDelegator {
    private final MemberRepository memberRepository;
    private OAuth2TokenSupport tokenSupport;

    public KakaoOpaqueTokenIntrospectorDelegator(MemberRepository memberRepository, List<OAuth2TokenSupport> tokenSupports) {
        this.memberRepository = memberRepository;
        for (OAuth2TokenSupport tokenSupport : tokenSupports) {
            if (tokenSupport.supports(AuthServer.KAKAO)) {
                this.tokenSupport = tokenSupport;
            }
        }
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            TokenValidationInfo tokenValidationInfo = this.tokenSupport.validateToken(token);
            Member findMember = this.memberRepository.findById(tokenValidationInfo.getEmail())
                    .orElseThrow(NoSuchElementException::new);// TODO: Erorr 메시지가 필요할지도?
            return new SingleAuthorityOAuth2Principal(
                    findMember.getId(),
                    Map.of(
                            OAuth2TokenIntrospectionClaimNames.IAT, tokenValidationInfo.getIssuedAt(),
                            OAuth2TokenIntrospectionClaimNames.EXP, tokenValidationInfo.getExpiredAt()
                    ),
                    findMember.getAuthority()
            );
        } catch (Exception e) {
            // TODO: Exception Handling을 좀 더 세밀하게 할 필요가 있을까?
            throw new OAuth2AuthenticationException(
                    // TODO: 임의의 errorCode, 그리고 url 대충 박음. 수정해야 함
                    new OAuth2Error("-1", "Invalid access token", "sample-url"),
                    e
            );
        }
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.KAKAO;
    }
}
