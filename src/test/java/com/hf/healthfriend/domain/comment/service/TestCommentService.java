package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@SpringBootTest
@Transactional
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
        Member postWriter = new Member("sample@post.writer");
        Member commentWriter = new Member("sample@comment.writer");

        this.memberRepository.save(commentWriter);
        this.memberRepository.save(postWriter);

        Post post = Post.builder()
                .title("sample-post")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .creationTime(LocalDateTime.now())
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

        assertThat(result.getPostId()).isEqualTo(post.getPostId());
        assertThat(result.getWriterId()).isEqualTo(commentWriter.getId());
        assertThat(result.getContent()).isEqualTo("sample-comment");
        assertThat(result.getCreationTime()).isAfterOrEqualTo(beforeStart);
    }

    @DisplayName("createComment - 생성 실패 - post is null")
    @Test
    void createComment_fail_sincePostIsNull_DataIntegrityViolationException() {
        // Given
        Member commentWriter = new Member("comment@writer.com");
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
        Member postWriter = new Member("sample@post.writer");
        this.memberRepository.save(postWriter);

        Post post = Post.builder()
                .title("sample-post")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .creationTime(LocalDateTime.now())
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
}