package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.constant.CookieConstants;
import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.propertyeditor.AuthServerEditor;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.OAuth2LoginResponseDto;
import com.hf.healthfriend.auth.oauth2.tokensupport.OAuth2TokenSupport;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import com.hf.healthfriend.global.spec.ApiErrorResponse;
import com.hf.healthfriend.global.spec.schema.OAuth2LoginResponseSchema;
import com.hf.healthfriend.global.util.HttpCookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth 2.0 Authorization Server에 등록된 Redirect URI Endpoint를 정의한 Controller.
 * 인가코드가 Query Parameter로 전해지면 Authorization Server에 Access Token, Refresh Token, Email 정보를
 * 받아와 요청이 들어온 클라이언트에 전달(Response)한다.
 *
 * @author PGD
 */
@Slf4j
@RestController
@RequestMapping("/oauth/code")
public class OAuth2RedirectionController {
    private final Map<AuthServer, OAuth2TokenSupport> tokenSupportByName;
    private final HttpCookieUtils cookieUtils;
    private final MemberService memberService;

    public OAuth2RedirectionController(List<OAuth2TokenSupport> oAuth2TokenSupports,
                                       HttpCookieUtils cookieUtils,
                                       MemberService memberService) {
        this.tokenSupportByName = new HashMap<>();
        for (OAuth2TokenSupport tokenSupport : oAuth2TokenSupports) {
            for (AuthServer authServer : AuthServer.values()) {
                if (tokenSupport.supports(authServer)) {
                    this.tokenSupportByName.put(authServer, tokenSupport);
                }
            }
        }

        this.cookieUtils = cookieUtils;
        this.memberService = memberService;
    }

    @InitBinder
    public void registerBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AuthServer.class, AuthServerEditor.getInstance());
    }

    @Operation(summary = "카카오 로그인 Redirect URI")
    @ApiResponses({
            @ApiResponse(
                    description = "카카오 로그인 성공 및 Access Token, Refresh Token, email 반환",
                    responseCode = "200",
                    headers = @Header(
                            name = "Set-Cookie",
                            description = "Refresh Token을 담은 HTTP-only, Secure, Same-site: None 쿠키"
                    ),
                    content = @Content(schema = @Schema(implementation = OAuth2LoginResponseSchema.class))
            ),
            @ApiResponse(
                    description = "유효하지 않은 인가코드로 인한 카카오 로그인 실패",
                    responseCode = "401",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 401,
                                        "statusCodeSeries": 4,
                                        "errorCode": 102,
                                        "errorName": "INVALID_CODE",
                                        "message": "유효하지 않은 인가코드입니다"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/kakao")
    public ResponseEntity<ApiBasicResponse<OAuth2LoginResponseDto>> get(@RequestParam("code") String code, HttpServletRequest request) {
        log.info("Kakao Login has been requested");
        return doTheSameThing(AuthServer.KAKAO, code, request.getRequestURL().toString());
    }

    @Operation(summary = "구글 로그인 Redirect URI")
    @ApiResponses({
            @ApiResponse(
                    description = "구글 로그인 성공 및 Access Token, Refresh Token, email 반환",
                    responseCode = "200",
                    headers = @Header(
                            name = "Set-Cookie",
                            description = "Refresh Token을 담은 HTTP-only, Secure, Same-site: None 쿠키"
                    ),
                    content = @Content(schema = @Schema(implementation = OAuth2LoginResponseSchema.class))
            ),
            @ApiResponse(
                    description = "유효하지 않은 인가코드로 인한 구글 로그인 실패",
                    responseCode = "401",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject("""
                                    {
                                        "statusCode": 401,
                                        "statusCodeSeries": 4,
                                        "errorCode": 102,
                                        "errorName": "INVALID_CODE",
                                        "message": "유효하지 않은 인가코드입니다"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/google")
    public ResponseEntity<ApiBasicResponse<OAuth2LoginResponseDto>> getGoogle(@RequestParam("code") String code, HttpServletRequest request) {
        log.info("Google Login has been requested");
        return doTheSameThing(AuthServer.GOOGLE, code, request.getRequestURL().toString());
    }

    private ResponseEntity<ApiBasicResponse<OAuth2LoginResponseDto>> doTheSameThing(AuthServer authServer, String code, String redirectUri) {
        OAuth2TokenSupport tokenSupport = this.tokenSupportByName.get(authServer);

        // 너무 간단한 로직이므로 Service 객체를 따로 정의하지 않고 여기서 했다.
        GrantedTokenInfo grantedTokenInfo = tokenSupport.grantToken(code, redirectUri);

        boolean memberExists = this.memberService.isMemberExists(grantedTokenInfo.getEmail());

        ResponseCookie refreshTokenCookie =
                this.cookieUtils.buildResponseCookie(CookieConstants.REFRESH_TOKEN_COOKIE_KEY.getString(),
                        grantedTokenInfo.getRefreshToken());

        // TODO: 302 Redirection Status를 set 해야 할 듯
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiBasicResponse.of(new OAuth2LoginResponseDto(!memberExists, grantedTokenInfo), HttpStatus.OK));
    }
}
