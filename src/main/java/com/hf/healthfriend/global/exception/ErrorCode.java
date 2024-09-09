package com.hf.healthfriend.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * <p>Error Code를 모아 놓은 enum 클래스. Error Code에 대해서는 <a href="https://github.com/team-HF/HF_backend/wiki/Error-Code">GitHub Wiki</a>
 * 를 참고한다.
 * <p>우선은 이 클래스에 모든 Error Code를 모아놓지만 Error Code가 많아질수록 하나의 파일 안에
 * Error Code를 모아놓으면 관리에 큰 애로사항이 생길 수 있다. Error Code가 많아지면 컴포넌트별로
 * Error Code를 따로 정의한 enum 클래스를 만들 예정이다.
 *
 * @author PGD
 * @see com.hf.healthfriend.domain.member.exceptionhandler.MemberExceptionHandlerControllerAdvice
 */

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
    UNAUTHENTICATED(100, "인증되지 않은 사용자입니다"),
    UNAUTHORIZED(101, "허용되지 않은 접근입니다"),
    INVALID_CODE(102, "유효하지 않은 인가코드입니다"),

    MEMBER_OF_THE_MEMBER_ID_NOT_FOUND(200, "memberId에 해당하는 회원이 없습니다"),
    MEMBER_ALREADY_EXISTS(201, "이미 존재하는 회원입니다");

    private final int code;
    private final String message;

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
