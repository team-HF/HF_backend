package com.hf.healthfriend.auth.oauth2.tokensupport;

import com.hf.healthfriend.auth.exception.InvalidCodeException;
import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("no-auth")
public class JsonBasedTokenSupport implements OAuth2TokenSupport {

    @Override
    public GrantedTokenInfo grantToken(String code, String redirectUri) throws InvalidCodeException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenValidationInfo validateToken(String token) throws RuntimeException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String requestEmail(String accessToekn) throws RuntimeException {
        log.trace("accessToken={}", accessToekn);
        JSONObject jsonObject = new JSONObject(accessToekn);
        return jsonObject.getString("email");
    }

    @Override
    public String refreshToken(String refreshToken) throws RuntimeException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return true;
    }
}
