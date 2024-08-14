package com.hf.healthfriend.auth.jwt.service;


import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.global.exception.custom.CustomJwtException;
import com.hf.healthfriend.global.exception.custom.MemberException;
import com.hf.healthfriend.global.exception.errorcode.CommonErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private SecretKey secretKey;

    @Value("${spring.jwt.secret}")
    private String secret;
    private final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14L;

    private final MemberRepository memberRepository;

    @PostConstruct
    private void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new CustomJwtException(CommonErrorCode.NOT_VALID_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            if (isRefreshTokenExpired(e)) {
                throw new CustomJwtException(CommonErrorCode.EXPIRED_REFRESH_TOKEN_EXCEPTION);
            }
            throw new CustomJwtException(CommonErrorCode.EXPIRED_ACCESS_TOKEN_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new CustomJwtException(CommonErrorCode.UNSUPPORTED_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new CustomJwtException(CommonErrorCode.MISMATCH_CLAIMS_EXCEPTION);
        }
    }

    public String createAccessToken(String email, String role) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .claim("token_type", "access_token")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken() {
        return Jwts.builder()
                .claim("token_type", "refresh_token")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 14);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private static boolean isRefreshTokenExpired(ExpiredJwtException e) {
        return e.getClaims().get("token_type").equals("refresh_token");
    }
}
