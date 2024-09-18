package com.hf.healthfriend.domain.comment.controller.exceptionhandler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum CommentErrorCode {
    COMMENT_NOT_FOUND(315, "존재하지 않는 댓글입니다."),
    POST_NOT_EXISTS(316, "존재하지 않는 포스트입니다.") // TODO
    ;

    private final int code;
    private final String message;

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
