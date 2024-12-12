package com.hf.healthfriend.domain.notification.dto;

import com.hf.healthfriend.domain.notification.constant.NotificationGetType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponse(
        String message,
        NotificationGetType type,
        LocalDateTime time,
        Long targetId
) {
}
