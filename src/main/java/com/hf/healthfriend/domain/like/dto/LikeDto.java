package com.hf.healthfriend.domain.like.dto;

import com.hf.healthfriend.domain.like.entity.Like;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class LikeDto {
    private Long likeId;

    @NotNull
    private Long memberId;

    @NotNull
    private Long postId;

    public LikeDto(@NotNull Long memberId, @NotNull Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }

    public static LikeDto of(Like like) {
        return new LikeDto(like.getLikeId(), like.getMember().getId(), like.getPost().getPostId());
    }
}
