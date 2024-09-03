package com.hf.healthfriend.auth.oauth2.service;

import com.hf.healthfriend.auth.oauth2.dto.response.GoogleUserDetails;
import com.hf.healthfriend.auth.oauth2.dto.response.KakaoUserDetails;
import com.hf.healthfriend.auth.oauth2.dto.response.Oauth2UserInfo;
import com.hf.healthfriend.auth.principal.PrincipalDetails;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.constant.Role;
import com.hf.healthfriend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // google, kakao
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Oauth2UserInfo oauth2UserInfo = getOauth2Response(registrationId, oAuth2User);

        Member member = memberService.saveOrUpdate(Member.builder()
                .email(oauth2UserInfo.getEmail())
                .role(Role.ROLE_NEW_USER)
                .build());

        PrincipalDetails principalDetails = new PrincipalDetails(member, oAuth2User.getAttributes());

        return principalDetails;
    }

    private static Oauth2UserInfo getOauth2Response(String registrationId, OAuth2User oAuth2User) {
        if (registrationId.equals("google")) {
            return new GoogleUserDetails(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            return new KakaoUserDetails(oAuth2User.getAttributes());
        }
        return null;
    }
}
