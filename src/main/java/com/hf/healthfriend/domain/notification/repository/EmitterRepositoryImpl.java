package com.hf.healthfriend.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {

    // SseEmitter 객체 저장용
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    // 연결이 끊겨있는 동안 발생하는 이벤트 객체 저장용 맵
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId,sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId,event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartsWithMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartsWithMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));    }

    @Override
    public void deleteEmitterById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterStartsWithMemberId(String memberId) {
        emitters.forEach(
                (key,emitter) -> {
                    if(key.startsWith(memberId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartsWithMemberId(String memberId) {
        eventCache.forEach(
                (key,emitter) -> {
                    if(key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
