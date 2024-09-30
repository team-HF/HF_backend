package com.hf.healthfriend.domain.post.dto.response;

import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
public record PostListObject(
        long postId,
        String category,
        String title,
        String content,
        LocalDateTime creationTime,
        long viewCount,
        long likeCount,
        long commentCount,
        String fitnessLevel
) {
    public static String calculateTimeAgo(LocalDateTime creationTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(creationTime, now);

        if (duration.toDays() > 0) {
            return duration.toDays() + "일 전";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + "시간 전";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + "분 전";
        } else {
            return duration.toSeconds() + "초 전";
        }
    }
}
