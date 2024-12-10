package com.hf.healthfriend.domain.notification.consumer;

import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.entity.Notification;
import com.hf.healthfriend.domain.notification.repository.NotificationRepository;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import com.hf.healthfriend.domain.notification.util.NotificationMessageGenerator;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBWorker {
    private final NotificationRepository notificationRepository;
    private final NotificationMessageGenerator messageGenerator;

    @SqsListener("${aws.sqs.dbQueueUrl}")
    public void consumeDbQueue(String message) {
        try {
            NotificationEvent event = JsonUtils.deserializeMessage(message);
            String noMessage = messageGenerator.generateMessage(event);

            Notification notification = Notification.builder()
                    .memberId(event.memberId())
                    .type(event.type())
                    .targetId(event.targetId())
                    .message(noMessage)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            log.info("DB 저장 성공: {}", event);
        } catch (Exception e) {
            log.error("DB 저장 실패: 메시지={}, 에러={}", message, e.getMessage());
            throw e; // SQS 자동 재시도를 위해 예외 던지기
        }
    }
}
