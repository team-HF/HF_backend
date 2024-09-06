package com.hf.healthfriend.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode{

    // Member
    NO_EXIST_EMAIL_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "email에 해당하는 member가 없습니다"),
    CONFLICT_EMAIL_MEMBER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 회원이 있습니다"),

    // Profile
    EXCEEDED_NICKNAME_CHANGE_LIMIT(HttpStatus.BAD_REQUEST, "닉네임 변경 제한 횟수는 5회입니다"),

    // JWT Errors
    NO_EXIST_REFRESH_TOKEN_COOKIE_EXCEPTION(HttpStatus.BAD_REQUEST, "Authrization-refresh 라는 key의 쿠키가 없거나 null입니다"),
    NO_EXIST_AUTHORIZATION_HEADER_EXCEPTION(HttpStatus.BAD_REQUEST, "Authorization 헤더가 없거나 Bearer로 시작하지 않습니다"),
    EXPIRED_ACCESS_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "Access token이 만료되었습니다. Refresh token을 사용하세요."),
    EXPIRED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "Refresh token이 만료되었습니다. 다시 로그인하세요."),
    NOT_VALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    MISMATCH_CLAIMS_EXCEPTION(HttpStatus.UNAUTHORIZED, "JWT 토큰의 클레임이 일치하지 않거나 토큰이 없습니다."),
    REFRESH_TOKEN_MISMATCH_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 Refresh token과 DB에 저장된 member의 token이 일치하는게 없습니다. 다시 로그인하세요.");



    private final HttpStatus httpStatus;
    private final String message;

    public int getCode() {
        return httpStatus.value();
    }
}
