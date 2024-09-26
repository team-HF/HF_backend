package com.hf.healthfriend.domain.like.exception;

import lombok.Getter;

@Getter
public class PostOrMemberNotExistsException extends RuntimeException {
    private final Long memberId;
    private final Long postId;

    public PostOrMemberNotExistsException(String message, Long memberId, Long postId) {
        super(message);
        this.memberId = memberId;
        this.postId = postId;
    }

    public PostOrMemberNotExistsException(String message, Throwable cause, Long memberId, Long postId) {
        super(message, cause);
        this.memberId = memberId;
        this.postId = postId;
    }

    public PostOrMemberNotExistsException(Throwable cause, Long memberId, Long postId) {
        super(cause);
        this.memberId = memberId;
        this.postId = postId;
    }
}
