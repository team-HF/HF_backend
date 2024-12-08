package com.hf.healthfriend.domain.notification.service;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ApplicationEventPublisher publisher;

    public void publishNotification(Long memberId, NotificationType type, String actor, Long targetId) {
        NotificationEvent event = new NotificationEvent(memberId, type, actor, targetId);
        publisher.publishEvent(event);
    }
}
