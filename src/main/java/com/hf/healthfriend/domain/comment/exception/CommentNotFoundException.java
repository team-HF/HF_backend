package com.hf.healthfriend.domain.comment.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {
    private Long commentId;

    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentNotFoundException(Throwable cause) {
        super(cause);
    }

    public CommentNotFoundException(String message, Long commentId) {
        super(message);
        this.commentId = commentId;
    }

    public CommentNotFoundException(String message, Throwable cause, Long commentId) {
        super(message, cause);
        this.commentId = commentId;
    }

    public CommentNotFoundException(Throwable cause, Long commentId) {
        super(cause);
        this.commentId = commentId;
    }
}
