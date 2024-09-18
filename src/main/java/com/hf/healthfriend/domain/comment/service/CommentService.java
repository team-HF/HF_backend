package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    public void deleteComment(Long commentId) throws NoSuchElementException { // TODO: NoSuchElementException이 맞을까?
        boolean success = this.commentRepository.deleteById(commentId);
        if (!success) {
            throw new NoSuchElementException();
        }
    }

    public List<CommentDto> getCommentsOfPost(Long postId) { // TODO: postId에 해당하는 Post가 없을 경우 어떻게?
        return this.commentRepository.findCommentsByPostId(postId)
                .stream()
                .map(CommentDto::of)
                .toList();
    }

    public List<CommentDto> getCommentsOfWriter(Long writerId) { // TODO: 존재하지 않는 Member일 경우 어떻게?
        return this.commentRepository.findCommentsByWriterId(writerId)
                .stream()
                .map(CommentDto::of)
                .toList();
    }

    public CommentDto updateComment(Long commentId, CommentUpdateDto updateDto) {
        Comment updatedComment = this.commentRepository.updateComment(commentId, updateDto);
        return CommentDto.of(updatedComment);
    }
}
