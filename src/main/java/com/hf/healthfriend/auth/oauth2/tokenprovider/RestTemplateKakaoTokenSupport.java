package com.hf.healthfriend.auth.oauth2.tokenprovider;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenValidationInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 추후 WebClient를 사용하든가, 다른 방식을 사용하는 객체로 대체할 예정
 */
@Slf4j
@Component
public class RestTemplateKakaoTokenSupport implements OAuth2TokenSupport {
    private static final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_ACCESS_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private static final String TOKEN_REQUEST_HEADER_KEY_GRANT_TYPE = "grant_type";
    private static final String TOKEN_REQUEST_HEADER_VALUE_GRANT_TYPE = "authorization_code";
    private static final String TOKEN_REQUEST_HEADER_KEY_CLIENT_ID = "client_id";
    private static final String TOKEN_REQUEST_HEADER_KEY_REDIRECT_URI = "redirect_uri";
    private static final String TOKEN_REQUEST_HEADER_KEY_CODE = "code";

    private final RestTemplate restTemplate;
    private final String kakaoClientId;

    public RestTemplateKakaoTokenSupport(RestTemplate restTemplate,
                                         @Value("${secret.oauth2.kakao.client-id.rest-api}") String kakaoClientId) {
        this.restTemplate = restTemplate;
        this.kakaoClientId = kakaoClientId;
    }

    @Override
    public GrantedTokenInfo grantToken(String code, String redirectUri) throws RuntimeException {
        LocalDateTime recordNow = LocalDateTime.now();

        JSONObject responseBody =
                new JSONObject(this.restTemplate.exchange(buildTokenRequestEntity(code, redirectUri), String.class).getBody());

        String accessToken = responseBody.getString("access_token");

        return new GrantedTokenInfo(
                accessToken,
                responseBody.getString("refresh_token"),
                recordNow.plus(responseBody.getInt("expires_in"), ChronoUnit.SECONDS),
                requestEmail(accessToken),
                AuthServer.KAKAO
        );
    }

    private RequestEntity<MultiValueMap<String, String>> buildTokenRequestEntity(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(TOKEN_REQUEST_HEADER_KEY_GRANT_TYPE, TOKEN_REQUEST_HEADER_VALUE_GRANT_TYPE);
        params.add(TOKEN_REQUEST_HEADER_KEY_CLIENT_ID, this.kakaoClientId);
        params.add(TOKEN_REQUEST_HEADER_KEY_REDIRECT_URI, redirectUri);
        params.add(TOKEN_REQUEST_HEADER_KEY_CODE, code);

        return RequestEntity.post(KAKAO_TOKEN_REQUEST_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(params);
    }

    private String requestEmail(String accessToken) {
        RequestEntity<String> requestEntity = RequestEntity.post(KAKAO_INFO_REQUEST_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body("property_keys=[\"kakao_account.email\"]");
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        return jsonObject.getJSONObject("kakao_account").getString("email");
    }

    @Override
    public TokenValidationInfo validateToken(String token) throws RuntimeException {
        Instant instantNow = Instant.now();
        return new TokenValidationInfo(
                requestEmail(token),
                instantNow.minusSeconds(60),
                requestForExpr(token)
        );
    }

    private Instant requestForExpr(String token) {
        Instant now = Instant.now();
        RequestEntity<Void> requestEntity = RequestEntity.get(KAKAO_ACCESS_TOKEN_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        JSONObject responseBody = new JSONObject(this.restTemplate.exchange(requestEntity, String.class).getBody());

        if (log.isTraceEnabled()) {
            log.trace("Response Content");
            System.out.println(responseBody);
        }
        return now.plus(responseBody.getInt("expires_in"), ChronoUnit.SECONDS);
    }

    @Override
    public boolean supports(AuthServer authServer) {
        return authServer == AuthServer.KAKAO;
    }
}