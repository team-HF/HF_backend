package com.hf.healthfriend.domain.notification.dto;

import lombok.Builder;

@Builder
public record Alarm(
        NotificationEvent event,
        String alarmMessage
) {
}
