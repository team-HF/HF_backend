package com.hf.healthfriend.domain.notification.listener;

import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.entity.Notification;
import com.hf.healthfriend.domain.notification.event.NotificationEvent;
import com.hf.healthfriend.domain.notification.repository.NotificationRepository;
import com.hf.healthfriend.domain.notification.util.NotificationMessageGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class NotificationListenerTest {
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void testHandleNotificationEvent() {
        // Given
        NotificationEvent event = new NotificationEvent(1L, NotificationType.ADD_COMMENT_TO_POST, "액터", 100L);

        // When
        notificationListener.handleNotificationEvent(event);

        // Then
        verify(notificationRepository).save(Mockito.any(Notification.class));
    }
}
