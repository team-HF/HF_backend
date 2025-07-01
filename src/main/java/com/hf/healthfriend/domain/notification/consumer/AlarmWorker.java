package com.hf.healthfriend.domain.notification.consumer;

import com.hf.healthfriend.domain.notification.dto.Alarm;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.entity.Notification;
import com.hf.healthfriend.domain.notification.repository.NotificationRepository;
import com.hf.healthfriend.domain.notification.service.NotificationSSEService;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import com.hf.healthfriend.domain.notification.util.NotificationMessageGenerator;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmWorker {

    private final NotificationSSEService notificationSseService;
    private final NotificationRepository notificationRepository;
    private final RedissonClient redissonClient;
    private final NotificationMessageGenerator messageGenerator;
    private final JsonUtils jsonUtils;

    @SqsListener("${aws.sqs.alarmQueueUrl}")
    @Transactional
    public void consumeAlarmMessage(String message) {
        NotificationEvent event = null; // event 객체를 try 블록 외부에서 선언
        try {
            event = jsonUtils.deserializeMessage(message);
            String notificationId = event.notificationId(); // 이벤트의 고유 ID

            // 중복 검증
            if (isDuplication(notificationId)) {
                log.info("이미 처리된 알림 ID 감지 (중복 스킵): {}", notificationId);
                return;
            }

            String alarmMessage = messageGenerator.generateMessage(event);
            Long memberId = event.memberId();

            // 2. SSE 알림 전송
            sendAlarm(event, alarmMessage, memberId);
            // 3. DB 저장
            saveAlarm(event, alarmMessage, memberId);

            markNotificationAsProcessed(notificationId);
        } catch (Exception e) {
            log.error("알림 처리 실패: 알림 ID={}, 메시지={}, 에러={}",
                    (event != null ? event.notificationId() : "N/A"), message, e.getMessage(), e);
            throw e;
        }
    }

    private boolean isDuplication(String notificationId) {
        return redissonClient.getBucket("processed_notification:" + notificationId).isExists();
    }

    private void markNotificationAsProcessed(String notificationId) {
        redissonClient.getBucket("processed_notification:" + notificationId)
                .set("true", 5, TimeUnit.MINUTES);
    }

    private void sendAlarm(NotificationEvent event,String alarmMessage, Long memberId){
        Alarm alarm = Alarm.builder()
                .event(event)
                .alarmMessage(alarmMessage)
                .build();
        notificationSseService.send(memberId, alarm);
        log.info("SSE 알림 전송 완료: {}", event);
    }

    private void saveAlarm(NotificationEvent event, String alarmMessage, Long memberId){
        Notification notification = Notification.builder()
                .memberId(memberId)
                .type(event.type())
                .targetId(event.targetId())
                .message(alarmMessage)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("DB 저장 완료: {}", event);
    }
}