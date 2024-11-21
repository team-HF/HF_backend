package com.hf.healthfriend.domain.post.dto.response;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.post.entity.Post;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostGetResponse(
        long postId,
        String postCategory,
        Long memberId,
        String title,
        String content,
        String imagePath,
        LocalDateTime createDate,
        Long viewCount,
        Long likeCount,
        List<CommentDto> comments
) {
    public static PostGetResponse of(Post post, List<CommentDto> comments, String imagePath) {
        return PostGetResponse.builder()
                .postId(post.getPostId())
                .postCategory(post.getCategory().name())
                .memberId(post.getMember().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imagePath(imagePath)
                .createDate(post.getCreationTime())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikesCount())
                .comments(comments)
                .build();
    }
}
