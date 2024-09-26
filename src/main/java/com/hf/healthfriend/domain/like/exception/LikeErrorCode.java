package com.hf.healthfriend.domain.like.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum LikeErrorCode {
    LIKE_NOT_EXISTS(40401, "L001", "존재하지 않는 Like"),
    DUPLICATE_LIKE_ATTEMPTION(40001, "L002", "한 회원에 한 게시물에 대해 중복해서 좋아요를 남기려 함 memberId=%d, postId=%d"),
    POST_OR_MEMBER_NOT_EXISTS(40002, "L003", "해당 글 또는 회원이 존재하지 않습니다. memberId=%d, postId=%d");

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
