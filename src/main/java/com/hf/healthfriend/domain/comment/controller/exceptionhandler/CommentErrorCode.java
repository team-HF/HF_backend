package com.hf.healthfriend.domain.comment.controller.exceptionhandler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum CommentErrorCode {
    COMMENT_NOT_FOUND(40400, "C001", "존재하지 않는 댓글입니다."),
    POST_NOT_EXISTS(40000, "C002", "존재하지 않는 포스트입니다."),
    MEMBER_NOT_EXISTS(40001, "C003", "존재하지 않는 회원입니다")
    ;

    private final int status;
    private final String code;
    private final String message;

    public int status() {
        return this.status;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
