package com.hf.healthfriend.auth.jwt.filter;

import com.hf.healthfriend.auth.jwt.service.JwtService;
import com.hf.healthfriend.auth.principal.PrincipalDetails;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.global.exception.custom.CustomJwtException;
import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Deprecated
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // filter를 거치고 싶지 않은 path를 여기서 관리함
        String[] excludePathLists = {"/favicon.ico", "/swagger-ui/index.html"};
        String[] excludePathStartsWithLists = {"/login", "/oauth2", "/api/member", "/v3", "/swagger-ui", "/ws"};

        String path = request.getRequestURI();

        // 해당 경로로 시작하는 uri에 대해서는 true를 반환하고 filter를 거치지 않음
        boolean startsWithExcludedPath = Arrays.stream(excludePathStartsWithLists).
                anyMatch(path::startsWith);

        // excludePathLists과 같은 uri로 매칭되면 true를 반환하고 filter를 거치지않음.
        boolean matchesExcludedPath = Arrays.stream(excludePathLists)
                .anyMatch((excludePath) -> excludePath.equals(path));

        return startsWithExcludedPath || matchesExcludedPath;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("요청 경로 : {}", request.getRequestURI());
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomJwtException(CommonErrorCode.NO_EXIST_AUTHORIZATION_HEADER_EXCEPTION);
        }

        String accesstoken = authorizationHeader.substring(7);

        if (jwtService.validateToken(accesstoken)) {
            //JWT 토큰을 파싱해서 member 정보를 가져옴
            String email = jwtService.getEmail(accesstoken);
            Member member = memberService.findMemberByEmail(email);

            // 해당 멤버를 authentication(인증) 해줌
            authentication(request, response, filterChain, member);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 유저를 authentication 해주는 메소드
     */
    private static void authentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Member member) throws IOException, ServletException {
        // PrincipalDetails에 유저 정보를 담기
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authenticationToken
                = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 세션에 사용자 등록, 해당 사용자는 스프링 시큐리티에 의해서 인증됨
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}

