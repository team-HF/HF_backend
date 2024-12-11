package com.hf.healthfriend.domain.notification.dto;

import com.hf.healthfriend.domain.notification.constant.NotificationGetType;
import lombok.Builder;

@Builder
public record NotificationResponse(
        String message,
        NotificationGetType type,
        Long targetId
) {
}
