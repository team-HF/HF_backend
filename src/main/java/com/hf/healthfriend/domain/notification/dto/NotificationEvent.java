package com.hf.healthfriend.domain.notification.dto;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import lombok.Builder;

@Builder
public record NotificationEvent(
        String notificationId,
        Long memberId,
        NotificationType type,
        String actor,
        Long targetId,
        Long timeStamp
        ) {
}
