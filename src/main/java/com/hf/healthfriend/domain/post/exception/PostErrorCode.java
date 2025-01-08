package com.hf.healthfriend.domain.post.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostErrorCode {
    INVALID_POST_REQUEST_FORMAT(40001, "POST001", "요청 형식이 잘못되었습니다."),

    POST_NOT_FOUND(40401, "POST002", "존재하지 않는 글입니다."),

    FORBIDDEN_POST_UPDATE(40301, "POST003", "해당 게시글을 수정할 권한이 없습니다."),

    FORBIDDEN_POST_DELETE(40302, "POST004", "해당 게시글을 삭제할 권한이 없습니다.");

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
