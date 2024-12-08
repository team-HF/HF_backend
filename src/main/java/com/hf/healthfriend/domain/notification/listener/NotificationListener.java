package com.hf.healthfriend.domain.notification.listener;

import com.hf.healthfriend.domain.notification.entity.Notification;
import com.hf.healthfriend.domain.notification.event.NotificationEvent;
import com.hf.healthfriend.domain.notification.repository.NotificationRepository;
import com.hf.healthfriend.domain.notification.util.NotificationMessageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationRepository notificationRepository;
    private final NotificationMessageGenerator messageGenerator;

    // TODO : 배치 저장이 낫지 않을까?
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            String message = messageGenerator.generateMessage(event);
            Notification notification = Notification.builder()
                    .memberId(event.memberId())
                    .type(event.type())
                    .targetId(event.targetId())
                    .message(message)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            // TODO : 클라이언트에 알림 전송 (SSE)
        } catch (Exception e) {
            log.error("Notification 처리 중 오류 발생. 타입: {}, 에러: {}", event.type(), e.getMessage(), e);
        }
    }

}
