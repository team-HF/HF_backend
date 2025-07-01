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
    private static final String DUP_CHECK_SCRIPT =
            "local key = KEYS[1] " +
                    "local currentTime = tonumber(ARGV[1]) " +
                    "local ttlSeconds = tonumber(ARGV[2]) " +
                    "local duplicationThreshold = tonumber(ARGV[3]) " + // 10ms

                    "local previousTime = redis.call('GET', key) " +
                    "if previousTime then " +
                    "  if (currentTime - tonumber(previousTime)) <= duplicationThreshold then " +
                    "    return 1 " + // 중복으로 판단 (true)
                    "  end " +
                    "end " +

                    "redis.call('SETEX', key, ttlSeconds, currentTime) " +
                    "return 0"; // 중복 아님 (false)

    @SqsListener("${aws.sqs.alarmQueueUrl}")
    @Transactional
    public void consumeAlarmMessage(String message) {
        try {
            NotificationEvent event = jsonUtils.deserializeMessage(message);
            // 1. 이미 처리된 메세지인지 체크
            if (isDuplication(event)) return;

            String alarmMessage = messageGenerator.generateMessage(event);
            Long memberId = event.memberId();

            // 2. SSE 알림 전송
            sendAlarm(event,alarmMessage,memberId);
            // 3. DB 저장
            saveAlarm(event,alarmMessage,memberId);

        } catch (Exception e) {
            log.error("알림 처리 실패: 메시지={}, 에러={}", message, e.getMessage(), e);
            throw e;
        }
    }

    private boolean isDuplication(NotificationEvent event){
        String key = event.notificationId();
        Long currentTime = event.timeStamp();
        long ttlSeconds = TimeUnit.MINUTES.toSeconds(5); // 5분 TTL
        long duplicationThreshold = 1000L; // 10ms

        RScript script = redissonClient.getScript();
        // KEYS: Redis 키 목록, ARGS: 스크립트에 전달될 인자 목록
        // ScriptOptions.Builder.returnResult(RScript.ReturnType.INTEGER)로 반환 타입 지정
        Long isDuplicate = script.eval(
                RScript.Mode.READ_WRITE, // 읽기/쓰기 권한 필요
                DUP_CHECK_SCRIPT,
                RScript.ReturnType.INTEGER, // 실제 자바 타입은 Long
                // Redisson 의 eval() 메서드는 List<Object> keys 로 명확하게 KEYS 배열을 요구하기 때문에
                // 단일 값 리스트로 넘긴다.
                Collections.singletonList(key),
                currentTime, // ARGV[1]
                ttlSeconds,  // ARGV[2]
                duplicationThreshold // ARGV[3]
        );

        if (isDuplicate == 1L) {
            log.info("중복 알림 감지 → SSE/DB 생략: {}", event.notificationId());
            return true;
        }
        return false;
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