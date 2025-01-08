package com.hf.healthfriend.domain.like.exception.errorcode;

import com.hf.healthfriend.global.exception.ErrorCodeSpecifiable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum LikeErrorCode implements ErrorCodeSpecifiable {
    LIKE_NOT_EXISTS(40401, "LIKE001", "존재하지 않는 Like"),
    DUPLICATE_LIKE_ATTEMPTION(40001, "LIKE002", "한 회원에 한 게시물에 대해 중복해서 좋아요를 남길 수 없습니다."),
    POST_OR_MEMBER_NOT_EXISTS(40002, "LIKE003", "해당 글 또는 회원이 존재하지 않습니다.");

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
        return this.status / 10000;
    }
}
