package com.hf.healthfriend.domain.post.dto.response;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.member.domain.Tier;
import com.hf.healthfriend.domain.post.entity.Post;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostGetResponse(
        long postId,
        long writerId,
        String postCategory,
        String writerNickname,
        Tier writerTier,
        String title,
        String content,
        String imagePath,
        LocalDateTime createDate,
        Long viewCount,
        Long likeCount,
        List<CommentDto> comments,
        Integer commentCount
) {
    public static PostGetResponse of(Post post, List<CommentDto> comments, String imagePath) {
        return PostGetResponse.builder()
                .postId(post.getPostId())
                .writerId(post.getMember().getId())
                .writerNickname(post.getMember().getNickname())
                .writerTier(post.getMember().getTier())
                .postCategory(post.getCategory().name())
                .title(post.getTitle())
                .content(post.getContent())
                .imagePath(imagePath)
                .createDate(post.getCreationTime())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikesCount())
                .comments(comments)
                .commentCount(comments.size())
                .build();
    }
}
