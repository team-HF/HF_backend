package com.hf.healthfriend.domain.notification.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationErrorCode {

    POST_NOT_FOUND(40401, "NOT001", "존재하지 않는 글에 대한 알림입니다."),
    COMMENT_NOT_FOUND(40402, "NOT002", "존재하지 않는 댓글에 대한 알림입니다."),
    MEMBER_NOT_FOUND(40403,"NOT003","존재하지 않는 멤버입니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

    public int statusCode() {return this.statusCode;}

    public String errorCode() {
        return this.errorCode;
    }

    public String message() {
        return this.message;
    }

}
