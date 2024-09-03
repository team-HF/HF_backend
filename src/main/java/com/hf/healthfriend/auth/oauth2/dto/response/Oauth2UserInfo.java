package com.hf.healthfriend.auth.oauth2.dto.response;

@Deprecated
public interface Oauth2UserInfo {
    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
