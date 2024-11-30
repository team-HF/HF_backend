package com.hf.healthfriend.domain.member.exception.errorcode;

import com.hf.healthfriend.global.exception.ErrorCodeSpecifiable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum MemberErrorCode implements ErrorCodeSpecifiable {
    MEMBER_NOT_FOUND(40401, "MB001", "memberId에 해당하는 회원이 없습니다"),
    MEMBER_CREATION_VALIDATION_ERROR(40001, "MB002", "회원 등록 에러"),
    FITNESS_LEVEL_UPDATE_NOT_ALLOWED(40002, "MB003", "운동 레벨 변경 거부"),
    MEMBER_ALREADY_EXISTS(40003, "MB004", "이미 존재하는 회원입니다");

    private final int status;
    private final String code;
    private final String message;

    @Override
    public int status() {
        return this.status;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public int statusCodeSeries() {
        return 4;
    }
}
