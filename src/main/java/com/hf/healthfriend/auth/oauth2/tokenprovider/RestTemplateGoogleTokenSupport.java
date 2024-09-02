package com.hf.healthfriend.auth.oauth2.tokenprovider;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 추후 WebClient를 사용하든가, 다른 방식을 사용하는 객체로 대체할 예정
 */
@Slf4j
@Component
public class RestTemplateGoogleTokenSupport implements OAuth2TokenSupport {
    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public RestTemplateGoogleTokenSupport(RestTemplate restTemplate,
                                          @Value("${secret.oauth2.google.client-id}") String clientId,
                                          @Value("${secret.oauth2.google.client-secret}") String clientSecret) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public GrantedTokenInfo grantToken(String code, String redirectUri) {

        LocalDateTime recordNow = LocalDateTime.now();

        JSONObject responseBody =
                new JSONObject(this.restTemplate.exchange(buildRequestEntityForToken(code, redirectUri), String.class).getBody());
        String accessToken = responseBody.getString("access_token");
        // TODO: refresh token이 없을 수 있으므로 JSONException을 처리하는 try ~ catch문 추가 (혹은 throws)
        String refreshToken = responseBody.getString("refresh_token");
        int expiresIn = responseBody.getInt("expires_in");
        String email = requestEmail(accessToken);

        if (log.isTraceEnabled()) {
            log.trace("Full response body");
            System.out.println(responseBody);
            log.trace("accessToken={}", accessToken);
            log.trace("refreshToken={}", refreshToken);
            log.trace("expiresIn={}", expiresIn);
            log.trace("email={}", email);
        }

        return new GrantedTokenInfo(
                accessToken,
                refreshToken,
                recordNow.plus(expiresIn, ChronoUnit.SECONDS), // TODO: 시간 단위가 뭔지 알아내야 함
                email,
                AuthServer.GOOGLE
        );
    }

    @Override
    public TokenValidationInfo validateToken(String token) throws RuntimeException {
        // TODO
        throw new UnsupportedOperationException();
    }

    private RequestEntity<String> buildRequestEntityForToken(String code, String redirectUri) {
        return RequestEntity.post(GOOGLE_TOKEN_REQUEST_URL)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(String.format("""
                        {
                            "code": "%s",
                            "client_id": "%s",
                            "client_secret": "%s",
                            "redirect_uri": "%s",
                            "grant_type": "authorization_code"
                        }
                        """, code, this.clientId, this.clientSecret, redirectUri));
    }

    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/userinfo/v2/me";

    private String requestEmail(String accessToken) {
        RequestEntity<Void> requestEntity = RequestEntity.get(GOOGLE_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
        JSONObject responseBody = new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());

        if (log.isTraceEnabled()) {
            log.trace("Full response body for request user info (for email)");
            System.out.println(responseBody);
        }

        return responseBody.getString("email");
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.GOOGLE;
    }
}
