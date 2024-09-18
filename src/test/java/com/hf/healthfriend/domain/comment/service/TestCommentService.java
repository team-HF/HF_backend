package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.repository.CommentRepository;
import com.hf.healthfriend.domain.member.constant.*;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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
        Member postWriter = new Member("sample@post.writer");
        setNecessaryFieldOfMemberToSampleData(postWriter, "샘플닉네임1");
        Member commentWriter = new Member("sample@comment.writer");
        setNecessaryFieldOfMemberToSampleData(commentWriter, "샘플닉네임2");

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
        setNecessaryFieldOfMemberToSampleData(commentWriter, "샘플닉네임");
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
        setNecessaryFieldOfMemberToSampleData(postWriter, "샘플닉네임");
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

    private void setNecessaryFieldOfMemberToSampleData(Member entity, String sampleNickname) {
        entity.setNickname(sampleNickname);
        entity.setBirthDate(LocalDate.of(1997, 9, 16));
        entity.setGender(Gender.MALE);
        entity.setIntroduction("");
        entity.setFitnessLevel(FitnessLevel.ADVANCED);
        entity.setCompanionStyle(CompanionStyle.SMALL);
        entity.setFitnessEagerness(FitnessEagerness.EAGER);
        entity.setFitnessObjective(FitnessObjective.BULK_UP);
        entity.setFitnessKind(FitnessKind.FUNCTIONAL);
    }

    @DisplayName("deleteComment - 성공")
    @Test
    void deleteComment_success() {
        // Given
        Member postWriter = new Member("sample@post.writer");
        setNecessaryFieldOfMemberToSampleData(postWriter, "샘플닉네임1");
        Member commentWriter = new Member("sample@comment.writer");
        setNecessaryFieldOfMemberToSampleData(commentWriter, "샘플닉네임2");

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
        Long newCommentId = result.getCommentId();

        // Then
        assertThatNoException()
                .isThrownBy(() -> this.commentService.deleteComment(result.getCommentId()));
    }

    @DisplayName("deleteComment - 없는 Comment를 삭제하려고 해서 실패")
    @Test
    void deleteComment_fail_noSuchElementException() {
        assertThatExceptionOfType(CommentNotFoundException.class)
                .isThrownBy(() -> this.commentService.deleteComment(1000L));
    }
}