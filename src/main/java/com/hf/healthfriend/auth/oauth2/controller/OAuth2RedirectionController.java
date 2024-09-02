package com.hf.healthfriend.auth.oauth2.controller;

import com.hf.healthfriend.auth.oauth2.constants.AuthServer;
import com.hf.healthfriend.auth.oauth2.dto.response.GrantedTokenInfo;
import com.hf.healthfriend.auth.oauth2.tokenprovider.OAuth2TokenSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oauth2/code")
public class OAuth2RedirectionController {
    private final Map<AuthServer, OAuth2TokenSupport> tokenSupportByName;

    public OAuth2RedirectionController(List<OAuth2TokenSupport> oAuth2TokenSupports) {
        this.tokenSupportByName = new HashMap<>();
        for (OAuth2TokenSupport tokenSupport : oAuth2TokenSupports) {
            for (AuthServer authServer : AuthServer.values()) {
                if (tokenSupport.supports(authServer)) {
                    this.tokenSupportByName.put(authServer, tokenSupport);
                }
            }
        }
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
