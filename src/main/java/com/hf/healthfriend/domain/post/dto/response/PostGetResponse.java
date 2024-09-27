package com.hf.healthfriend.domain.post.dto.response;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostGetResponse(
        long postId,
        String postCategory,
        String memberId,
        String title,
        String content,
        LocalDateTime createDate,
        Long viewCount,
        Long likeCount,
        List<CommentDto> comments
) {
}
