package com.hf.healthfriend.domain.post.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public record PostGetResponse(
        long postId,
        String postCategory,
        String memberId,
        String title,
        String content,
        LocalDateTime createDate,
        Long view_count
) {
}
