package com.hf.healthfriend.auth.oauth2.dto.response;

import com.hf.healthfriend.domain.member.entity.Role;

@Deprecated
public interface Oauth2UserInfo {
    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
