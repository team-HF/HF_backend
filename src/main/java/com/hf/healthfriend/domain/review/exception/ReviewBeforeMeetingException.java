package com.hf.healthfriend.domain.review.exception;

import java.time.LocalDateTime;

public class ReviewBeforeMeetingException extends RuntimeException {
    private final LocalDateTime currentTime;
    private final LocalDateTime meetingTime;

    public ReviewBeforeMeetingException(LocalDateTime currentTime, LocalDateTime meetingTime) {
        this.currentTime = currentTime;
        this.meetingTime = meetingTime;
    }

    public ReviewBeforeMeetingException(String message, LocalDateTime currentTime, LocalDateTime meetingTime) {
        super(message);
        this.currentTime = currentTime;
        this.meetingTime = meetingTime;
    }

    public ReviewBeforeMeetingException(String message, Throwable cause, LocalDateTime currentTime, LocalDateTime meetingTime) {
        super(message, cause);
        this.currentTime = currentTime;
        this.meetingTime = meetingTime;
    }

    public ReviewBeforeMeetingException(Throwable cause, LocalDateTime currentTime, LocalDateTime meetingTime) {
        super(cause);
        this.currentTime = currentTime;
        this.meetingTime = meetingTime;
    }
}
