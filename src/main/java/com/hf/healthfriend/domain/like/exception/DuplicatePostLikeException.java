package com.hf.healthfriend.domain.like.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DuplicatePostLikeException extends RuntimeException {
    private long postId;
    private long memberId;

    public DuplicatePostLikeException(long postId, long memberId) {
        super();
        this.postId = postId;
        this.memberId = memberId;
    }

    public DuplicatePostLikeException(long postId, long memberId, String message) {
        super(message);
        this.postId = postId;
        this.memberId = memberId;
    }

    public DuplicatePostLikeException(long postId, long memberId, String message, Throwable cause) {
        super(message, cause);
        this.postId = postId;
        this.memberId = memberId;
    }

    public DuplicatePostLikeException(long postId, long memberId, Throwable cause) {
        super(cause);
        this.postId = postId;
        this.memberId = memberId;
    }
}
