package com.hf.healthfriend.domain.notification.consumer;

import com.hf.healthfriend.domain.notification.dto.Alarm;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.service.NotificationSSEService;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import com.hf.healthfriend.domain.notification.util.NotificationMessageGenerator;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseWorker {

    private final NotificationSSEService notificationSseService;
    private final NotificationMessageGenerator messageGenerator;
    private final JsonUtils jsonUtils;

    @SqsListener("${aws.sqs.alarmQueueUrl}")
    public void consumeAlarmMessage(String message) {
        try {
            NotificationEvent event = jsonUtils.deserializeMessage(message);
            String alarmMessage = messageGenerator.generateMessage(event);
            Long memberId = event.memberId();
            Alarm alarm = Alarm.builder()
                    .event(event)
                    .alarmMessage(alarmMessage)
                    .build();
            notificationSseService.send(memberId,alarm);
            log.info("클라이언트에 실시간 알림 전송 완료: {}", event);
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}