package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.oauth2.constant.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.propertyeditor.AuthServerEditor;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.dto.response.OAuth2LoginResponseDto;
import com.hf.healthfriend.auth.oauth2.tokenprovider.OAuth2TokenSupport;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.util.HttpCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oauth2/code")
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
        return doTheSameThing(AuthServer.KAKAO, code, request.getRequestURI());
    }

    @GetMapping("/google")
    public ResponseEntity<OAuth2LoginResponseDto> getGoogle(@RequestParam("code") String code, HttpServletRequest request) {
        return doTheSameThing(AuthServer.GOOGLE, code, request.getRequestURI());
    }

    private ResponseEntity<OAuth2LoginResponseDto> doTheSameThing(AuthServer authServer, String code, String redirectUri) {
        OAuth2TokenSupport tokenSupport = this.tokenSupportByName.get(authServer);

        // 너무 간단한 로직이므로 Service 객체를 따로 정의하지 않고 여기서 했다.
        GrantedTokenInfo grantedTokenInfo = tokenSupport.grantToken(code, redirectUri);
        MemberCreationResponseDto memberCreationResponseDto = this.memberService.createMemberIfNotExists(
                new MemberCreationRequestDto(grantedTokenInfo.getEmail(), grantedTokenInfo.getEmail(), null));

        // TODO: 302 Redirection Status를 set 해야 할 듯
        return ResponseEntity.ok()
                .body(new OAuth2LoginResponseDto(memberCreationResponseDto, grantedTokenInfo));
    }
}
