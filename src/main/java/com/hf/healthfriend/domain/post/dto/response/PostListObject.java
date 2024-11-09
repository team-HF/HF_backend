package com.hf.healthfriend.domain.post.dto.response;

import lombok.Builder;

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
        String fitnessLevel,
        // TODO : dto에서 빼기, 프로필 추가
        long totalPageSize
) {
}
