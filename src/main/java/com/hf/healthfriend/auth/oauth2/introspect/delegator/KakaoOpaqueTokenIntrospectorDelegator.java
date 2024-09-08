package com.hf.healthfriend.auth.oauth2.introspect.delegator;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import com.hf.healthfriend.auth.oauth2.principal.SingleAuthorityOAuth2Principal;
import com.hf.healthfriend.auth.oauth2.tokensupport.KakaoOAuth2TokenSupport;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOpaqueTokenIntrospectorDelegator implements OpaqueTokenIntrospectorDelegator {
    private final MemberService memberService;
    private final KakaoOAuth2TokenSupport tokenSupport;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        // TODO: GoogleOpaqueTokenIntrospectorDelegator와 똑같은 로직이 반복된다. 굳이 둘로 나눌 필요는 없을지도
        TokenValidationInfo tokenValidationInfo = this.tokenSupport.validateToken(token);
        MemberDto memberDto = this.memberService.findMember(tokenValidationInfo.getEmail());
        return new SingleAuthorityOAuth2Principal(
                memberDto.getMemberId(),
                memberDto.getRole()
        );
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.KAKAO;
    }

    @Override
    public AuthServer getSupportingAuthServer() {
        return AuthServer.KAKAO;
    }
}
