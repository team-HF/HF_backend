package com.hf.healthfriend.domain.comment.exception;

import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {
    private final Long postId;

    public PostNotFoundException(Long postId, String message) {
        super(message);
        this.postId = postId;
    }

    public PostNotFoundException(Long postId, String message, Throwable cause) {
        super(message, cause);
        this.postId = postId;
    }

    public PostNotFoundException(Long postId, Throwable cause) {
        super(cause);
        this.postId = postId;
    }
}
