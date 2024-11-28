package com.hf.healthfriend.domain.comment.exception.errorcode;

import com.hf.healthfriend.global.exception.ErrorCodeSpecifiable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum CommentErrorCode implements ErrorCodeSpecifiable {
    COMMENT_NOT_FOUND(40401, "CMT001", "존재하지 않는 댓글입니다."),
    POST_NOT_EXISTS(40402, "CMT002", "존재하지 않는 포스트입니다."),
    PARENT_COMMENT_NOT_FOUND(40403, "CMT003", "존재하지 않는 댓글에 대한 답글입니다.")
    ;

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
