package com.hf.healthfriend.domain.like.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DuplicateCommentLikeException extends RuntimeException {
    private long commentId;
    private long memberId;

    public DuplicateCommentLikeException(long commentId, long memberId) {
        super();
        this.commentId = commentId;
        this.memberId = memberId;
    }

    public DuplicateCommentLikeException(long commentId, long memberId, String message) {
        super(message);
        this.commentId = commentId;
        this.memberId = memberId;
    }

    public DuplicateCommentLikeException(long commentId, long memberId, String message, Throwable cause) {
        super(message, cause);
        this.commentId = commentId;
        this.memberId = memberId;
    }

    public DuplicateCommentLikeException(long commentId, long memberId, Throwable cause) {
        super(cause);
        this.commentId = commentId;
        this.memberId = memberId;
    }
}
