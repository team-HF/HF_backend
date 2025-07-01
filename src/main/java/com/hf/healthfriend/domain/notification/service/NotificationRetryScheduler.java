package com.hf.healthfriend.domain.notification.service;

import com.hf.healthfriend.domain.notification.publisher.NotificationPublisher;
import com.hf.healthfriend.domain.notification.repository.NotificationFailoverRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationFailoverRepository failoverRepository;
    private final NotificationPublisher publisher;
    private final RedissonClient redissonClient;

    @Scheduled(fixedDelay = 600000) // 1분마다
    public void retryFailedNotifications() {
        // Redis 에서 key 목록을 가져와서 순회 (Prefix 기준 조회 필요)
        Iterable<String> iterableKeys = redissonClient.getKeys().getKeysByPattern("notification:fail:*");
        List<String> keys = new ArrayList<>();
        iterableKeys.forEach(keys::add);
        if (!keys.isEmpty()) {
            log.info("발행 실패 메세지 재시도 시작, 개수 : {}", keys.size());
            for (String key : keys) {
                String notificationId = key.replace("notification:fail:", "");
                failoverRepository.getFail(notificationId).ifPresent(event -> {
                    publisher.publishNotification(event);
                    failoverRepository.deleteFail(notificationId);
                });
            }
        }
    }
}