package com.hf.healthfriend.domain.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommentErrorCode {
    COMMENT_NOT_FOUND(40401, "CMT001", "존재하지 않는 댓글입니다."),

    POST_NOT_FOUND(40402, "CMT002", "존재하지 않는 포스트입니다."),

    MEMBER_NOT_EXISTS(40403, "CMT003", "존재하지 않는 회원입니다"),

    PARENT_COMMENT_NOT_FOUND(40404, "CMT004", "존재하지 않는 댓글에 대한 답글입니다."),

    FORBIDDEN_COMMENT_UPDATE(40301,"CMT005","댓글을 수정할 권한이 없습니다."),

    FORBIDDEN_COMMENT_DELETE(40302,"CMT006","댓글을 삭제할 권한이 없습니다."),

    INVALID_COMMENT_REQUEST_FORMAT(40001,"CMT007","요청 형식이 잘못되었습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

    public int statusCode() {return this.statusCode;}

    public String errorCode() {
        return this.errorCode;
    }

    public String message() {
        return this.message;
    }

}
