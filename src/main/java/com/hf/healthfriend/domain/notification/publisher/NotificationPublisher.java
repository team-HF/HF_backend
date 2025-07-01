package com.hf.healthfriend.domain.notification.publisher;

import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.repository.NotificationFailoverRepository;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    // newSingleThreadScheduledExecutor : 싱글 스레드로서 순서 보장
    // newScheduledThreadPool : 병렬 처리로서 처리량 보장
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(16);

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

    // 지수 백오프 방식으로 재시도
    private void attemptSend(NotificationEvent event, String message, int attempt) {
        sqsAsyncClient.sendMessage(builder -> builder
                        .queueUrl(alarmQueueUrl)
                        .messageBody(message)
                )
                .orTimeout(3, TimeUnit.SECONDS)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.warn("SQS 전송 실패 attempt={} → {}", attempt, ex.getMessage());
                        if (attempt < 3) {
                            long delaySeconds = (long) Math.pow(2, attempt); // 1초 → 2초 → 4초
                            scheduler.schedule(() -> attemptSend(event, message, attempt + 1),
                                    delaySeconds, TimeUnit.SECONDS);
                            log.info("재시도 {}회차 {}초 후에 실행", attempt + 1, delaySeconds);
                        } else {
                            log.error("SQS 전송 3회 실패, 레디스로 이동 eventId={}, error={}",
                                    event.notificationId(), ex.getMessage());
                            failoverRepository.saveFail(event);
                        }
                    } else {
                        log.info("SQS 전송 성공: messageId={}, notificationId={}",
                                res.messageId(), event.notificationId());
                    }
                });
    }
}