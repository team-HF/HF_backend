package com.hf.healthfriend.domain.notification.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화 실패: " + e.getMessage(), e);
        }
    }

    public NotificationEvent deserializeMessage(String message) {
        try {
            JSONObject snsMessage = new JSONObject(message);
            String messageContent = snsMessage.getString("Message");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(messageContent, NotificationEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 역직렬화 실패", e);
        }
    }
}