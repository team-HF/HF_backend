package com.hf.healthfriend.domain.notification.repository;

import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String eventCacheId, Object event);

    Map<String, SseEmitter> findAllEmitterStartsWithMemberId(String memberId);

    Map<String, Object> findAllEventCacheStartsWithMemberId(String memberId);

    void deleteEmitterById(String emitterId);

    void deleteAllEmitterStartsWithMemberId(String memberId);

    void deleteAllEventCacheStartsWithMemberId(String memberId);
}
