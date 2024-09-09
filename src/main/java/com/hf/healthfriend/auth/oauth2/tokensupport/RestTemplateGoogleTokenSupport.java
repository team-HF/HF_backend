package com.hf.healthfriend.auth.oauth2.tokensupport;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 추후 WebClient를 사용하든가, 다른 방식을 사용하는 객체로 대체할 예정
 */
@Slf4j
@Component
public class RestTemplateGoogleTokenSupport implements GoogleOAuth2TokenSupport {
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

        log.trace("Full Response Body\n{}", responseBody);

        String accessToken = responseBody.getString("access_token");

        String refreshToken = null;
        try {
            refreshToken = responseBody.getString("refresh_token");
            log.trace("refreshToken={}", refreshToken);
        } catch (JSONException e) {
            // TODO: JSONException이 발생하는 이유가 여러가지 있을 수 있으므로 여러 Exception에 대응
            log.warn("Refresh Token 파싱 중 문제 발생", e);
        }
        int expiresIn = responseBody.getInt("expires_in");
        String email = requestEmail(accessToken);

        if (log.isTraceEnabled()) {
            log.trace("accessToken={}", accessToken);
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

    @Override
    public String requestEmail(String accessToken) {
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

    private static final String GOOGLE_TOKEN_VALIDATION_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo";

    @Override
    public TokenValidationInfo validateToken(String token) throws RuntimeException {
        String builtUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_TOKEN_VALIDATION_URL)
                .queryParam("access_token", token)
                .encode()
                .toUriString();
        RequestEntity<Void> requestEntity = RequestEntity.get(builtUrl).build();

        // TODO: 유효하지 않은 토큰일 경우 400 Error가 발생한다. 이 400 Error를 AuthenticationException으로 처리해야 하며 유효하지 않은 토큰이라는 메시지를 포함해야 한다.
        JSONObject responseBody = new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());
        return new TokenValidationInfo(
                responseBody.getString("email")
        );
    }

    @Override
    public String refreshToken(String refreshToken) throws RuntimeException {
        // 유효하지 않은 리프레시 토큰일 경우 400 에러 발생

        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("refresh_token", refreshToken);
        bodyParams.add("grant_type", "refresh_token");
        bodyParams.add("client_id", this.clientId);
        bodyParams.add("client_secret", this.clientSecret);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(GOOGLE_TOKEN_REQUEST_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(bodyParams);

        JSONObject responseBody = new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());

        if (log.isTraceEnabled()) {
            log.trace("Response Body:\n {}", responseBody);
        }

        return responseBody.getString("access_token");
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.GOOGLE;
    }
}
