package com.hf.healthfriend.auth.oauth2.principal;

import com.hf.healthfriend.domain.member.constant.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SingleAuthorityOAuth2Principal implements OAuth2AuthenticatedPrincipal {
    private final String principal;
    private final Map<String, Object> attributes;
    private final List<GrantedAuthority> authority;

    public SingleAuthorityOAuth2Principal(String principal, String authority) {
        this(principal, new SimpleGrantedAuthority(authority));
    }

    public SingleAuthorityOAuth2Principal(String principal, Role role) {
        this(principal, role.name());
    }

    public SingleAuthorityOAuth2Principal(String principal, GrantedAuthority authority) {
        this(principal, Map.of(), authority);
    }

    public SingleAuthorityOAuth2Principal(String principal, Map<String, Object> attributes, Role role) {
        this(principal, attributes, role.name());
    }

    public SingleAuthorityOAuth2Principal(String principal, Map<String, Object> attributes, String authority) {
        this(principal, attributes, new SimpleGrantedAuthority(authority));
    }

    public SingleAuthorityOAuth2Principal(String principal, Map<String, Object> attributes, GrantedAuthority authority) {
        this.principal = principal;
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
        return this.principal;
    }
}
