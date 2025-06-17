package com.hf.healthfriend.domain.notification.publisher;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.repository.NotificationFailoverRepository;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
                )
                .orTimeout(3, TimeUnit.SECONDS) // 응답이 없어도 3초 후에는 반드시 whenComplete()가 호출되게
                .whenComplete((res, ex) -> { // 응답이 왔을 때
                    if (ex != null) {
                        log.warn("SQS 알림 전송 실패 attempt={} → {}", attempt, ex.getMessage());

                        // 타임아웃 또는 네트워크 오류와 같은 예외 처리
                        if (ex instanceof TimeoutException) {
                            log.error("타임아웃 발생, 재시도 attempt={} → {}", attempt, ex.getMessage());
                            if (attempt < 3) {
                                attemptSend(event, message, attempt + 1); // 재시도
                            } else {
                                log.error("타임아웃 재전송 실패, 레디스로 이동 eventId={}, error={}", event.notificationId(), ex.getMessage());
                                failoverRepository.saveFail(event); // 재시도 3회 실패 시 레디스로 이동
                            }
                        } else {
                            // 일반적인 오류 (예: 메시지 포맷 오류 등)
                            if (attempt < 3) {
                                attemptSend(event, message, attempt + 1); // 재시도
                            } else {
                                log.error("SQS 알림 전송 3회 실패. eventId={}, error={}", event.notificationId(), ex.getMessage());
                                failoverRepository.saveFail(event);
                            }
                        }
                    } else {
                        log.info("SQS 알림 전송 성공: messageId={}, notificationId={}", res.messageId(), event.notificationId());
                    }
                });
    }
}