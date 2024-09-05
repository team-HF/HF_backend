package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.constant.SecurityConstants;
import com.hf.healthfriend.auth.oauth2.dto.request.WrappingRefreshTokenWithCookieRequestDto;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenRefreshResponseDto;
import com.hf.healthfriend.auth.oauth2.tokensupport.OAuth2TokenSupport;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.util.HttpCookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/oauth/token")
@RequiredArgsConstructor
public class OAuth2TokenController {
    private final List<OAuth2TokenSupport> tokenSupports;
    private final HttpCookieUtils httpCookieUtils;

    @PostMapping("/refresh-token/wrap-with-cookie")
    public ResponseEntity<ApiBasicResponse<Void>> wrapWithCookie(@RequestBody WrappingRefreshTokenWithCookieRequestDto dto) {
        var refreshToken = dto.getRefreshToken();
        var responseCookie =
                this.httpCookieUtils.buildResponseCookie(SecurityConstants.REFRESH_TOKEN_COOKIE_KEY.get(), refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiBasicResponse.of(HttpStatus.OK, "Refresh Token에 쿠키 저장 완료"));
    }

    @GetMapping("/refresh")
    public TokenRefreshResponseDto refreshToken(@CookieValue("refresh_token") String refreshToken) {
        String regrantedAccessToken = null;
        for (OAuth2TokenSupport tokenSupport : tokenSupports) {
            try {
                regrantedAccessToken = tokenSupport.refreshToken(refreshToken);
            } catch (Exception e) { // TODO: 세밀한 예외 처리
                log.warn("{}가 처리할 수 없는 Refresh Token", tokenSupport.getClass());
            }
        }

        if (regrantedAccessToken == null) {
            throw new IllegalArgumentException(); // TODO: 임시 예외 / 저 정확한 예외를 던져야 함
            // TODO: 유효하지 않은 Refresh Token일 경우 401을 던질지 400을 던질지 고민해야 함
        }

        return new TokenRefreshResponseDto(regrantedAccessToken);
    }
}