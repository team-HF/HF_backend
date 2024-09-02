package com.hf.healthfriend.auth.oauth2.dto.response;

import com.hf.healthfriend.domain.member.entity.Role;
import lombok.Getter;

import java.util.Map;

@Deprecated
@Getter
public class KakaoUserDetails implements Oauth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserDetails(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    /**
     * Provider(카카오) 측에서 제공하는 사용자 구분 id 값
     */
    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }
}
