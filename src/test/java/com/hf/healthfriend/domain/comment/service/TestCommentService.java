package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.exception.PostNotFoundException;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({
        "local-dev",
        "secret",
        "constants",
        "priv"
})
class TestCommentService {

    @Autowired
    CommentService commentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("createComment - 생성 성공")
    @Test
    void createComment_succeededToCreate() {
        // Given
        Member postWriter = SampleEntityGenerator.generateSampleMember("sample@post.writer", "샘플닉네임1");
        Member commentWriter = SampleEntityGenerator.generateSampleMember("sample@comment.writer", "샘플닉네임2");

        this.memberRepository.save(commentWriter);
        this.memberRepository.save(postWriter);

        Post post = Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .member(postWriter)
                .build();

        this.postRepository.save(post);
        log.info("post={}", post);

        // When
        CommentCreationRequestDto requestDto = CommentCreationRequestDto.builder()
                .writerId(commentWriter.getId())
                .content("sample-comment")
                .build();

        LocalDateTime beforeStart = LocalDateTime.now();

        // Then
        CommentCreationResponseDto result = this.commentService.createComment(post.getPostId(), requestDto);
        log.info("result={}", result);

        assertThat(result.postId()).isEqualTo(post.getPostId());
        assertThat(result.writerId()).isEqualTo(commentWriter.getId());
        assertThat(result.content()).isEqualTo("sample-comment");
        assertThat(result.creationTime()).isAfterOrEqualTo(beforeStart);
    }

    @DisplayName("createComment - 생성 실패 - post is null")
    @Test
    void createComment_fail_sincePostIsNull_DataIntegrityViolationException() {
        // Given
        Member commentWriter = SampleEntityGenerator.generateSampleMember("comment@writer.com", "샘플닉네임");
        this.memberRepository.save(commentWriter);

        // When
        CommentCreationRequestDto requestDto = CommentCreationRequestDto.builder()
                .writerId(commentWriter.getId())
                .content("sample-content")
                .build();

        // Then
        assertThatExceptionOfType(InvalidDataAccessApiUsageException.class)
                .isThrownBy(() -> this.commentService.createComment(null, requestDto));
    }

    @DisplayName("createcomment - 생성 실패 - DB에 없는 회원 ID로 생성")
    @Test
    void createComment_fail_sinceTransientMember_DataIntegrityViolationException() {
        // Given
        Member postWriter = SampleEntityGenerator.generateSampleMember("sample@post.writer", "샘플닉네임");
        this.memberRepository.save(postWriter);

        Post post = Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .member(postWriter)
                .build();

        this.postRepository.save(post);

        // When
        CommentCreationRequestDto requestDto = CommentCreationRequestDto.builder()
                .writerId(23518524L)
                .content("good-content")
                .build();

        // Then
        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> this.commentService.createComment(post.getPostId(), requestDto));
    }

    @DisplayName("getCommentsOfPost - 존재하지 않는 글의 댓글을 조회하려고 할 때 PostNotFoundException 발생")
    @Test
    void getCommentsOfPost_failure_PostNotFoundException() {
        assertThatExceptionOfType(PostNotFoundException.class)
                .isThrownBy(() -> this.commentService.getCommentsOfPost(1111L, CommentSortType.LATEST));
    }

    @DisplayName("getCommentsOfWriter - 존재하지 않는 회원의 댓글을 조회하려 할 때 MemberNotFoundException 발생")
    @Test
    void getCommentsOfWriter_failure_MemberNotFoundException() {
        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> this.commentService.getCommentsOfWriter(11111L));
    }

    @DisplayName("deleteComment - 성공")
    @Test
    void deleteComment_success() {
        // Given
        Member postWriter = SampleEntityGenerator.generateSampleMember("sample@post.writer", "샘플닉네임1");
        Member commentWriter = SampleEntityGenerator.generateSampleMember("sample@comment.writer", "샘플닉네임2");

        this.memberRepository.save(commentWriter);
        this.memberRepository.save(postWriter);

        Post post = Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .member(postWriter)
                .build();

        this.postRepository.save(post);
        log.info("post={}", post);

        CommentCreationRequestDto requestDto = CommentCreationRequestDto.builder()
                .writerId(commentWriter.getId())
                .content("sample-comment")
                .build();

        LocalDateTime beforeStart = LocalDateTime.now();

        CommentCreationResponseDto result = this.commentService.createComment(post.getPostId(), requestDto);
        log.info("result={}", result);

        // When
        Long newCommentId = result.commentId();

        // Then
        assertThatNoException()
                .isThrownBy(() -> this.commentService.deleteComment(result.commentId()));
    }

    @DisplayName("deleteComment - 없는 Comment를 삭제하려고 해서 실패")
    @Test
    void deleteComment_fail_noSuchElementException() {
        assertThatExceptionOfType(CommentNotFoundException.class)
                .isThrownBy(() -> this.commentService.deleteComment(1000L));
    }
}