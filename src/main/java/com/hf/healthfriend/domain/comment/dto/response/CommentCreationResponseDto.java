package com.hf.healthfriend.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class CommentCreationResponseDto {
    private final long commentId;
    private final long postId;
    private final long writerId;
    private final String content;
    private LocalDateTime creationTime;
}
