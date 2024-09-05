package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import com.hf.healthfriend.auth.oauth2.principal.SingleAuthorityOAuth2Principal;
import com.hf.healthfriend.auth.oauth2.tokensupport.GoogleOAuth2TokenSupport;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleOpaqueTokenIntrospectorDelegator implements OpaqueTokenIntrospectorDelegator {
    private final MemberService memberService;
    private final GoogleOAuth2TokenSupport tokenSupport;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        TokenValidationInfo tokenValidationInfo = this.tokenSupport.validateToken(token);
        MemberDto memberDto = this.memberService.findMember(tokenValidationInfo.getEmail());
        return new SingleAuthorityOAuth2Principal(
                memberDto.getMemberId(),
                memberDto.getRole()
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
