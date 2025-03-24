package com.hf.healthfriend.domain.notification.consumer;

import com.hf.healthfriend.domain.notification.dto.Alarm;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.entity.Notification;
import com.hf.healthfriend.domain.notification.repository.NotificationRepository;
import com.hf.healthfriend.domain.notification.service.NotificationSSEService;
import com.hf.healthfriend.domain.notification.util.NotificationMessageGenerator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmWorker {

    private final NotificationSSEService notificationSseService;
    private final NotificationRepository notificationRepository;
    private final NotificationMessageGenerator messageGenerator;

    @Async // 비동기 실행
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) throws InterruptedException {
        try {
            String alarmMessage = messageGenerator.generateMessage(event);
            Long memberId = event.memberId();

            // 1. SSE 알림 전송
            Alarm alarm = Alarm.builder()
                    .event(event)
                    .alarmMessage(alarmMessage)
                    .build();
            notificationSseService.send(memberId, alarm);
            log.info("✅ SSE 알림 전송 완료: {}", event);

            // 2. DB 저장
            Notification notification = Notification.builder()
                    .memberId(event.memberId())
                    .type(event.type())
                    .targetId(event.targetId())
                    .message(alarmMessage)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            log.info("✅ DB 저장 완료: {}", event);

        } catch (Exception e) {
            log.error("❌ 알림 처리 실패: event={}, 에러={}", event, e.getMessage(), e);
            throw e; // 예외 발생 시 재시도 가능하도록 예외 던지기
        }
    }
}