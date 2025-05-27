package com.hf.healthfriend.domain.notification.consumer;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class DlqWorker {

    private final SqsTemplate sqsTemplate;
    private final SqsAsyncClient sqsAsyncClient;

    // TODO: 큐 생성해서 URL 변경해야 됨
    private final String dlqUrl = "";
    private final String alarmQueueName = "";

    @Scheduled(fixedDelay = 300000) // 5분마다 실행
    public void retryDlqMessages() {
        Collection<Message<String>> messages = sqsTemplate.receiveMany(options -> {
            options.queue(dlqUrl);
            options.maxNumberOfMessages(10);
        }, String.class);

        if (messages.isEmpty()) {
            log.info("DLQ에 메시지가 없습니다.");
            return;
        }

        for (Message<String> message : messages) {
            String payload = message.getPayload();
            String receiptHandle = (String) message.getHeaders().get("ReceiptHandle");
            try {
                // 1. 원래 큐로 전송
                sqsTemplate.send(alarmQueueName, payload);
                log.info("DLQ 메시지 원래 큐로 재전송 완료: {}", payload);

                // 2. AWS SDK로 DLQ에서 삭제
                if (receiptHandle != null) {
                    sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(dlqUrl)
                            .receiptHandle(receiptHandle)
                            .build());
                    log.info("DLQ 메시지 삭제 완료: {}", receiptHandle);
                } else {
                    log.warn("ReceiptHandle이 없어 삭제를 건너뜀");
                }

            } catch (Exception e) {
                log.error("DLQ 메시지 처리 실패: {}", payload, e);
            }
        }
    }
}