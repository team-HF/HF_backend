package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.exception.PostNotFoundException;
import com.hf.healthfriend.domain.comment.repository.CommentJpaRepository;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

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

    public void deleteComment(Long commentId) throws CommentNotFoundException {
        this.commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getCommentsOfPost(Long postId, CommentSortType sortType) {
        if (!this.postRepository.existsById(postId)) {
            throw new PostNotFoundException(postId, "postId에 해당하는 Post가 없음");
        }

        return this.commentJpaRepository.findCommentsByPostIdWithSorting(postId,sortType)
                .stream()
                .map(CommentDto::of)
                .toList();
    }

    public List<CommentDto> getCommentsOfWriter(Long writerId) {
        if (!this.memberRepository.existsById(writerId)) {
            throw new MemberNotFoundException(writerId, "writerId에 해당하는 Member가 없음");
        }

        return this.commentRepository.findCommentsByWriterId(writerId)
                .stream()
                .map(CommentDto::of)
                .toList();
    }

    public CommentDto updateComment(Long commentId, CommentUpdateDto updateDto) throws CommentNotFoundException {
        Comment updatedComment = this.commentRepository.updateComment(commentId, updateDto);
        return CommentDto.of(updatedComment);
    }
}
