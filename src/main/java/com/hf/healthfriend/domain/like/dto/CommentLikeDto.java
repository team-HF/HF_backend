package com.hf.healthfriend.domain.like.dto;

import com.hf.healthfriend.domain.like.entity.Like;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class CommentLikeDto {
    private Long likeId;

    @NotNull
    private Long memberId;

    @NotNull
    private Long commentId;

    public CommentLikeDto(@NotNull Long memberId, @NotNull Long commentId) {
        this.memberId = memberId;
        this.commentId = commentId;
    }

    public static CommentLikeDto of(Like like) {
        return new CommentLikeDto(like.getLikeId(), like.getMember().getId(), like.getComment().getCommentId());
    }
}
