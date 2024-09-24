package com.hf.healthfriend.domain.comment.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentCreationResponseDto(
        long commentId,
        long postId,
        long writerId,
        String content,
        LocalDateTime creationTime
) {
}
