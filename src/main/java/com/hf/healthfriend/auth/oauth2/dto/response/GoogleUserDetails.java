package com.hf.healthfriend.auth.oauth2.dto.response;

import com.hf.healthfriend.domain.member.entity.Role;
import lombok.Getter;

import java.util.Map;

@Getter
public class GoogleUserDetails implements Oauth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserDetails(Map<String, Object> attribute) {
        this.attributes = attribute;
    }

    @Override
    public String getProvider() {

        return "google";
    }

    /**
     * Provider(구글) 측에서 제공하는 사용자 구분 id 값
     */
    @Override
    public String getProviderId() {

        return attributes.get("sub").toString();
    }

    @Override
    public String getEmail() {

        return attributes.get("email").toString();
    }

    @Override
    public String getName() {

        return attributes.get("name").toString();
    }
}
