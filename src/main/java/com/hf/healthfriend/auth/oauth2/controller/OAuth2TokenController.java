package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.constant.CookieConstants;
import com.hf.healthfriend.auth.exception.InvalidRefreshTokenException;
import com.hf.healthfriend.auth.oauth2.dto.request.WrappingRefreshTokenWithCookieRequestDto;
import com.hf.healthfriend.auth.oauth2.dto.response.TokenRefreshResponseDto;
import com.hf.healthfriend.auth.oauth2.tokensupport.OAuth2TokenSupport;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import com.hf.healthfriend.global.spec.schema.TokenRefreshResponseSchema;
import com.hf.healthfriend.global.util.HttpCookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Deprecated
    @PostMapping("/refresh-token/wrap-with-cookie")
    public ResponseEntity<ApiBasicResponse<Void>> wrapWithCookie(@RequestBody WrappingRefreshTokenWithCookieRequestDto dto) {
        var refreshToken = dto.getRefreshToken();
        var responseCookie =
                this.httpCookieUtils.buildHttpOnlyResponseCookie(CookieConstants.COOKIE_NAME_REFRESH_TOKEN.getString(), refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiBasicResponse.of(HttpStatus.OK, "Refresh Token에 쿠키 저장 완료"));
    }

    @Operation(
            summary = "Refresh Token을 통한 Access Token 재발급",
            description = "HTTP-only Cookie에 담겨 있는 Refresh Token을 통해 Access Token을 재발급받는다."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Access Token 재발급 성공",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = TokenRefreshResponseSchema.class),
                            examples = {
                                    @ExampleObject("""
                                            {
                                                "statusCode": 200,
                                                "statusCodeSeries": 2,
                                                "message": null,
                                                "content": {
                                                    "accessToken": "fawoigh42ihsioahoidasasodifh..."
                                                }
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    description = "유효하지 않은 Refresh Token으로 인한 Access Token 재발급 실패",
                    responseCode = "401",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 401,
                                        "statusCodeSeries": 4,
                                        "errorCode": 103,
                                        "errorName": "INVALID_REFRESH_TOKEN",
                                        "message": "유효하지 않은 Refresh Token입니다"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/refresh")
    public ResponseEntity<ApiBasicResponse<TokenRefreshResponseDto>> refreshToken(
            @CookieValue("refresh_token") String refreshToken) {
        String regrantedAccessToken = null;
        for (OAuth2TokenSupport tokenSupport : tokenSupports) {
            try {
                regrantedAccessToken = tokenSupport.refreshToken(refreshToken);
            } catch (Exception e) { // TODO: 세밀한 예외 처리
                log.warn("{}가 처리할 수 없는 Refresh Token", tokenSupport.getClass());
            }
        }

        if (regrantedAccessToken == null) {
            throw new InvalidRefreshTokenException("유효하지 않은 Refresh Token");
        }

        return ResponseEntity.ok(ApiBasicResponse.of(new TokenRefreshResponseDto(regrantedAccessToken), HttpStatus.OK));
    }
}
