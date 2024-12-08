package com.hf.healthfriend.domain.notification.service;

import static org.mockito.Mockito.verify;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.event.NotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testPublishNotification() {
        // Given
        Long memberId = 1L;
        NotificationType type = NotificationType.ADD_COMMENT_TO_POST;
        String actor = "액터";
        Long targetId = 100L;

        // When
        notificationService.publishNotification(memberId, type, actor, targetId);

        // Then
        verify(publisher).publishEvent(new NotificationEvent(memberId, type, actor, targetId));
    }
}
