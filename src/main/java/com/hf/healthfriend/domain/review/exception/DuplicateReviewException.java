package com.hf.healthfriend.domain.review.exception;

public class DuplicateReviewException extends RuntimeException {
    private Long matchingId;

    public DuplicateReviewException(String message, Long matchingId) {
        super(message);
        this.matchingId = matchingId;
    }

    public DuplicateReviewException(String message, Throwable cause, Long matchingId) {
        super(message, cause);
        this.matchingId = matchingId;
    }

    public DuplicateReviewException(Throwable cause, Long matchingId) {
        super(cause);
        this.matchingId = matchingId;
    }
}
