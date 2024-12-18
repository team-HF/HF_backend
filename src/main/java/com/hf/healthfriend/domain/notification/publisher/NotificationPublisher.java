package com.hf.healthfriend.domain.notification.publisher;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {
    private final SnsClient snsClient;
    private final JsonUtils jsonUtils;

    @Value("${aws.sns.topicArn}")
    private String topicArn;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishNotification(Long memberId, NotificationType type, String actor, Long targetId) {
        NotificationEvent event = new NotificationEvent(memberId, type, actor, targetId);
        String message = jsonUtils.serialize(event);
        snsClient.publish(builder -> builder
                .topicArn(topicArn)
                .message(message)
        );
        log.info("SNS에 알림 이벤트 퍼블리싱: memberId={}, type={}, actor={}, targetId={}",
                memberId, type, actor, targetId);
    }
}