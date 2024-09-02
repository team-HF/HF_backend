package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.propertyeditor.AuthServerEditor;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.tokenprovider.OAuth2TokenSupport;
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

    public OAuth2RedirectionController(List<OAuth2TokenSupport> oAuth2TokenSupports,
                                       HttpCookieUtils cookieUtils) {
        this.tokenSupportByName = new HashMap<>();
        for (OAuth2TokenSupport tokenSupport : oAuth2TokenSupports) {
            for (AuthServer authServer : AuthServer.values()) {
                if (tokenSupport.supports(authServer)) {
                    this.tokenSupportByName.put(authServer, tokenSupport);
                }
            }
        }

        this.cookieUtils = cookieUtils;
    }

    @InitBinder
    public void registerBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AuthServer.class, AuthServerEditor.getInstance());
    }

    @GetMapping("/kakao")
    public ResponseEntity<GrantedTokenInfo> get(@RequestParam("code") String code, HttpServletRequest request) {
        OAuth2TokenSupport tokenSupport = this.tokenSupportByName.get(AuthServer.KAKAO);
        // TODO: 302 Redirection Status를 set 해야 할 듯
        return ResponseEntity.ok()
                .body(tokenSupport.grantToken(code, request.getRequestURI()));
    }

    @GetMapping("/google")
    public ResponseEntity<GrantedTokenInfo> getGoogle(@RequestParam("code") String code, HttpServletRequest request) {
        OAuth2TokenSupport tokenSupport = this.tokenSupportByName.get(AuthServer.GOOGLE);
        // TODO: 302 Redirection Status를 set 해야 할 듯
        return ResponseEntity.ok()
                .body(tokenSupport.grantToken(code, request.getRequestURI()));
    }
}
