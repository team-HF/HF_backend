package com.hf.healthfriend.auth.authentication;

import com.hf.healthfriend.auth.oauth2.principal.SingleAuthorityOAuth2Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class NoAuthAuthentication implements Authentication {
    private final SingleAuthorityOAuth2Principal principal;

    public NoAuthAuthentication(String name, GrantedAuthority authority) {
        this.principal = new SingleAuthorityOAuth2Principal(name, authority);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.principal.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return this.principal.getName();
    }
}
