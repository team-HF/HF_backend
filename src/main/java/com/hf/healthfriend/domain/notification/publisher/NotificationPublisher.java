package com.hf.healthfriend.domain.notification.publisher;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.repository.NotificationFailoverRepository;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final SqsAsyncClient sqsAsyncClient;
    private final JsonUtils jsonUtils;
    private final NotificationFailoverRepository failoverRepository;

    @Value("${aws.sqs.alarmQueueUrl}")
    private String alarmQueueUrl;

    public void publishNotification(NotificationEvent event) {
        String message = jsonUtils.serialize(event);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                attemptSend(event, message, 1);
            }
        });
    }

    private void attemptSend(NotificationEvent event, String message, int attempt) {
        sqsAsyncClient.sendMessage(builder -> builder
                .queueUrl(alarmQueueUrl)
                .messageBody(message)
                .messageGroupId("notify-group")
                .messageDeduplicationId(event.notificationId())
        ).whenComplete((res, ex) -> {
            if (ex != null) {
                log.warn("SQS 알림 전송 실패 attempt={} → {}", attempt, ex.getMessage());
                if (attempt < 3) {
                    attemptSend(event, message, attempt + 1); // 재시도
                } else {
                    log.error("SQS 알림 전송 3회 실패. eventId={}, error={}", event.notificationId(), ex.getMessage());
                    failoverRepository.saveFail(event);
                }
            } else {
                log.info("SQS 알림 전송 성공: messageId={}, notificationId={}", res.messageId(), event.notificationId());
            }
        });
    }
}