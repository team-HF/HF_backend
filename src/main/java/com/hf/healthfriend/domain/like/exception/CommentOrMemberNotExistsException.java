package com.hf.healthfriend.domain.like.exception;

import lombok.Getter;

@Getter
public class CommentOrMemberNotExistsException extends RuntimeException {
    private final Long memberId;
    private final Long commentId;

    public CommentOrMemberNotExistsException(String message, Long memberId, Long commentId) {
        super(message);
        this.memberId = memberId;
        this.commentId = commentId;
    }

    public CommentOrMemberNotExistsException(String message, Throwable cause, Long memberId, Long commentId) {
        super(message, cause);
        this.memberId = memberId;
        this.commentId = commentId;
    }

    public CommentOrMemberNotExistsException(Throwable cause, Long memberId, Long commentId) {
        super(cause);
        this.memberId = memberId;
        this.commentId = commentId;
    }
}
