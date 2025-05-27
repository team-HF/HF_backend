package com.hf.healthfriend.domain.notification.service;

import com.hf.healthfriend.domain.notification.publisher.NotificationPublisher;
import com.hf.healthfriend.domain.notification.repository.NotificationFailoverRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {

    private final NotificationFailoverRepository failoverRepository;
    private final NotificationPublisher publisher;
    private final RedissonClient redissonClient;

    @Scheduled(fixedDelay = 60000) // 1분마다
    public void retryFailedNotifications() {
        // Redis에서 key 목록을 가져와서 순회 (Prefix 기준 조회 필요)
        Collection<String> keys = (Collection<String>) redissonClient.getKeys().getKeysByPattern("notification:fail:*");

        for (String key : keys) {
            String notificationId = key.replace("notification:fail:", "");

            failoverRepository.getFail(notificationId).ifPresent(event -> {
                publisher.publishNotification(event); // 또는 기존 publishNotification 재호출
                failoverRepository.deleteFail(notificationId);
            });
        }
    }
}