package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentCreationResponseDto createComment(Long postId, CommentCreationRequestDto requestDto)
            throws DataIntegrityViolationException {
        Comment toSave = new Comment(
                Post.builder().postId(postId).build(),
                new Member(requestDto.getWriterId()),
                requestDto.getContent()
        );
        Comment newComment = this.commentRepository.save(toSave);
        log.info("[Comment Creation] postId={}, commenterId={}", postId, requestDto.getWriterId());

        return CommentCreationResponseDto.builder()
                .commentId(newComment.getCommentId())
                .postId(newComment.getPost().getPostId())
                .writerId(newComment.getWriter().getId())
                .content(newComment.getContent())
                .creationTime(newComment.getCreationTime())
                .build();
    }
}
