package com.hf.healthfriend.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class CommentDto {
    private final Long commentId;
    private final Long postId;
    private final Long writerId;
    private final String content;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModified;
    private final boolean isDeleted;
}
