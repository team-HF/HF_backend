package com.hf.healthfriend.domain.notification.dto;

import com.hf.healthfriend.domain.notification.constant.NotificationType;

public record NotificationEvent(
        Long memberId,
        NotificationType type,
        String actor,
        Long targetId) {
}
