package com.hf.healthfriend.domain.notification.constant;

import lombok.Getter;

@Getter
public enum NotificationType {
    ADD_COMMENT_TO_COMMENT,
    ADD_COMMENT_TO_POST,
    ADD_LIKE_TO_COMMENT,
    ADD_LIKE_TO_POST,
    POPULAR_POST,
    MATCH_REQUEST,
    MATCH_ACCEPT,
    MATCH_REJECT,
    MATCH_PUNK,
    MATCH_END_REVIEW

}