package com.hf.healthfriend.domain.comment.dto;

import com.hf.healthfriend.domain.comment.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class CommentDto {
    private Long commentId;
    private Long postId;
    private Long writerId;
    private String content;
    private LocalDateTime creationTime;
    private LocalDateTime lastModified;

    public static CommentDto of(Comment entity) {
        return CommentDto.builder()
                .commentId(entity.getCommentId())
                .postId(entity.getPost().getPostId())
                .writerId(entity.getWriter().getId())
                .content(entity.getContent())
                .creationTime(entity.getCreationTime())
                .lastModified(entity.getLastModified())
                .build();
    }
}
