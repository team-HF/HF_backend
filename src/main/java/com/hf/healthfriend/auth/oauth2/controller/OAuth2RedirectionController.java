package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.propertyeditor.AuthServerEditor;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.OAuth2LoginResponseDto;
import com.hf.healthfriend.auth.oauth2.tokensupport.OAuth2TokenSupport;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.util.HttpCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
 * @deprecated 토큰을 받는 요청을 프론트엔드에서 처리하기로 협의했으므로 이 컨트롤러는 필요없게 됨
 */
@Deprecated
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

    @GetMapping("/kakao")
    public ResponseEntity<OAuth2LoginResponseDto> get(@RequestParam("code") String code, HttpServletRequest request) {
        log.info("Kakao Login has been requested");
        return doTheSameThing(AuthServer.KAKAO, code, request.getRequestURL().toString());
    }

    @GetMapping("/google")
    public ResponseEntity<OAuth2LoginResponseDto> getGoogle(@RequestParam("code") String code, HttpServletRequest request) {
        log.info("Google Login has been requested");
        return doTheSameThing(AuthServer.GOOGLE, code, request.getRequestURL().toString());
    }

    private ResponseEntity<OAuth2LoginResponseDto> doTheSameThing(AuthServer authServer, String code, String redirectUri) {
        OAuth2TokenSupport tokenSupport = this.tokenSupportByName.get(authServer);

        // 너무 간단한 로직이므로 Service 객체를 따로 정의하지 않고 여기서 했다.
        GrantedTokenInfo grantedTokenInfo = tokenSupport.grantToken(code, redirectUri);
        MemberCreationResponseDto memberCreationResponseDto = this.memberService.createMember(
                new MemberCreationRequestDto(grantedTokenInfo.getEmail(), grantedTokenInfo.getEmail(), null));

        // TODO: 302 Redirection Status를 set 해야 할 듯
        return ResponseEntity.ok()
                .body(new OAuth2LoginResponseDto(memberCreationResponseDto, grantedTokenInfo));
    }
}
