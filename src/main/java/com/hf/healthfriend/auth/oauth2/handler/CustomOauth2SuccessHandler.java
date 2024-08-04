package com.hf.healthfriend.auth.oauth2.handler;

import com.hf.healthfriend.auth.jwt.service.JwtService;
import com.hf.healthfriend.auth.principal.PrincipalDetails;
import com.hf.healthfriend.domain.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails oauth2User = (PrincipalDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = oauth2User.getAuthorities();

        String email = oauth2User.getEmail();
        String role = authorities.iterator().next().getAuthority();
        System.out.println("role = " + role);


        String accessToken = jwtService.createAccessToken(email, role);
        String refreshToken =  jwtService.createRefreshToken();
        memberService.saveRefresh(email, refreshToken);

        response.setHeader("Authorization", accessToken);
        response.addCookie(jwtService.createCookie("Authorization-refresh", refreshToken));

        String redirectUrl = "http://localhost:3000/mainpage";
        if ("ROLE_NEW_USER".equals(role)) {
            redirectUrl = "http://localhost:3000/sign-up"; // 새로운 유저이면 추가로 회원가입 진행
        }

        response.sendRedirect(redirectUrl);

        log.info("OAuth2 로그인에 성공하였습니다. 이메일 : {}",  oauth2User.getEmail());
        log.info("OAuth2 로그인에 성공하였습니다. Access Token : {}",  accessToken);
        log.info("OAuth2 로그인에 성공하였습니다. Refresh Token : {}",  refreshToken);
        log.info("redirect_url : {}", redirectUrl);
    }
}
