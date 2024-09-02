package com.hf.healthfriend.auth.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SingleAuthorityOAuth2Principal implements OAuth2AuthenticatedPrincipal {
    private final String name;
    private final Map<String, Object> attributes;
    private final List<GrantedAuthority> authority;

    public SingleAuthorityOAuth2Principal(String name, Map<String, Object> attributes, String authority) {
        this.name = name;
        this.attributes = attributes;
        this.authority = List.of(new SimpleGrantedAuthority(authority));
    }

    public SingleAuthorityOAuth2Principal(String name, Map<String, Object> attributes, GrantedAuthority authority) {
        this.name = name;
        this.attributes = attributes;
        this.authority = List.of(authority);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authority;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
