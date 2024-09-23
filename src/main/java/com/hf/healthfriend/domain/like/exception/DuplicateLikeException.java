package com.hf.healthfriend.domain.like.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DuplicateLikeException extends RuntimeException {
    private long postId;
    private long memberId;

    public DuplicateLikeException(long postId, long memberId) {
        super();
        this.postId = postId;
        this.memberId = memberId;
    }

    public DuplicateLikeException(long postId, long memberId, String message) {
        super(message);
        this.postId = postId;
        this.memberId = memberId;
    }

    public DuplicateLikeException(long postId, long memberId, String message, Throwable cause) {
        super(message, cause);
        this.postId = postId;
        this.memberId = memberId;
    }

    public DuplicateLikeException(long postId, long memberId, Throwable cause) {
        super(cause);
        this.postId = postId;
        this.memberId = memberId;
    }
}
