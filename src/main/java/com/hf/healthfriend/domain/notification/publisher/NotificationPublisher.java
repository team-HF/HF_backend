package com.hf.healthfriend.domain.notification.publisher;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishNotification(Long memberId, NotificationType type, String actor, Long targetId) {
        NotificationEvent event = new NotificationEvent(memberId, type, actor, targetId);

        // 트랜잭션 커밋 이후 실행되도록 이벤트 발행
        eventPublisher.publishEvent(event);
        log.info("알림 이벤트 발행: memberId={}, type={}, actor={}, targetId={}", memberId, type, actor, targetId);
    }
}