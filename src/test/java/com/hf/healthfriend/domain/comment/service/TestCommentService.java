package com.hf.healthfriend.domain.comment.service;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.dto.request.CommentCreationRequestDto;
import com.hf.healthfriend.domain.comment.dto.response.CommentCreationResponseDto;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.exception.CommentException;
import com.hf.healthfriend.domain.comment.repository.CommentJpaRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.exception.PostException;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.testutil.RedisTestConfig;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@Import(RedisTestConfig.class)
class TestCommentService {

    @Autowired
    CommentService commentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentJpaRepository commentJpaRepository;

    @MockBean
    RedissonClient redissonClient;

    @Autowired
    private EntityManager entityManager;

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
        assertThatExceptionOfType(PostException.class)
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
        assertThatExceptionOfType(CommentException.class)
                .isThrownBy(() -> this.commentService.deleteComment(1000L));
    }

    private CommentCreationResponseDto createComment(Post post, Member writer, String content, Long parentCommentId) {
        CommentCreationRequestDto requestDto = CommentCreationRequestDto.builder()
                .writerId(writer.getId())
                .content(content)
                .parentCommentId(parentCommentId)
                .build();
        return this.commentService.createComment(post.getPostId(), requestDto);
    }

    @DisplayName("대댓글이 트리 구조로 잘 생성되는지 테스트")
    @Test
    void replyComment_treeStructureTest() {
        Member parentCommentWriter = SampleEntityGenerator.generateSampleMember("parent@writer.com", "부모 댓글 작성자");
        Member childCommentWriter = SampleEntityGenerator.generateSampleMember("child@writer.com", "자식 댓글 작성자");
        Member grandchildCommentWriter = SampleEntityGenerator.generateSampleMember("grandchild@writer.com", "손자 댓글 작성자");

        this.memberRepository.save(parentCommentWriter);
        this.memberRepository.save(childCommentWriter);
        this.memberRepository.save(grandchildCommentWriter);

        Post post = Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .member(parentCommentWriter)
                .build();

        this.postRepository.save(post);

        // 댓글 생성
        CommentCreationResponseDto parentComment = createComment(post, parentCommentWriter, "부모 댓글", null);
        CommentCreationResponseDto childComment = createComment(post, childCommentWriter, "자식 댓글", parentComment.commentId());
        CommentCreationResponseDto grandchildComment = createComment(post, grandchildCommentWriter, "손자 댓글", childComment.commentId());

        entityManager.flush();
        entityManager.clear();
        // When
        List<CommentDto> comments = this.commentService.getCommentsOfPost(post.getPostId(), CommentSortType.LATEST);

        // Then
        // 부모 댓글 검증
        assertThat(comments).hasSize(1); // 최상위 댓글은 부모 댓글만 존재
        CommentDto parentDto = comments.get(0);
        assertThat(parentDto.getCommentId()).isEqualTo(parentComment.commentId());
        assertThat(parentDto.getReplies()).hasSize(1); // 자식 댓글 1개

        // 자식 댓글 검증
        CommentDto childDto = parentDto.getReplies().get(0);
        assertThat(childDto.getCommentId()).isEqualTo(childComment.commentId());
        assertThat(childDto.getReplies()).hasSize(1); // 손자 댓글 1개

        // 손자 댓글 검증
        CommentDto grandchildDto = childDto.getReplies().get(0);
        assertThat(grandchildDto.getCommentId()).isEqualTo(grandchildComment.commentId());
        assertThat(grandchildDto.getReplies()).isEmpty(); // 손자 댓글은 자식이 없음

    }

    @DisplayName("부모 댓글 삭제 시 자식 댓글도 재귀적으로 soft delete 되는지 테스트")
    @Test
    void deleteComment_withReplies_treeStructureDeletionTest() {
        // Given
        Member parentCommentWriter = SampleEntityGenerator.generateSampleMember("parent@writer.com", "부모 댓글 작성자");
        Member childCommentWriter = SampleEntityGenerator.generateSampleMember("child@writer.com", "자식 댓글 작성자");
        Member grandchildCommentWriter = SampleEntityGenerator.generateSampleMember("grandchild@writer.com", "손자 댓글 작성자");

        // 멤버 저장
        this.memberRepository.save(parentCommentWriter);
        this.memberRepository.save(childCommentWriter);
        this.memberRepository.save(grandchildCommentWriter);

        // 게시글 저장
        Post post = Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.WORKOUT_CERTIFICATION)
                .member(parentCommentWriter)
                .build();
        this.postRepository.save(post);

        // 댓글 생성
        CommentCreationResponseDto parentComment = createComment(post, parentCommentWriter, "부모 댓글", null);
        CommentCreationResponseDto childComment = createComment(post, childCommentWriter, "자식 댓글", parentComment.commentId());
        CommentCreationResponseDto grandchildComment = createComment(post, grandchildCommentWriter, "손자 댓글", childComment.commentId());

        entityManager.flush();
        entityManager.clear();

        // When
        // 삭제 전 상태 확인
        assertThat(commentJpaRepository.existsByCommentIdAndIsDeletedFalse(parentComment.commentId())).isTrue();
        assertThat(commentJpaRepository.existsByCommentIdAndIsDeletedFalse(childComment.commentId())).isTrue();
        assertThat(commentJpaRepository.existsByCommentIdAndIsDeletedFalse(grandchildComment.commentId())).isTrue();

        // 부모 댓글 삭제
        this.commentService.deleteComment(parentComment.commentId());

        // Then
        // 삭제 후 상태 확인
        assertThat(commentJpaRepository.existsByCommentIdAndIsDeletedFalse(parentComment.commentId())).isFalse();
        assertThat(commentJpaRepository.existsByCommentIdAndIsDeletedFalse(childComment.commentId())).isFalse();
        assertThat(commentJpaRepository.existsByCommentIdAndIsDeletedFalse(grandchildComment.commentId())).isFalse();

        // 부모 댓글 is_deleted 상태 확인
        Optional<Comment> deletedParentComment = commentJpaRepository.findById(parentComment.commentId());
        assertThat(deletedParentComment).isPresent();
        assertThat(deletedParentComment.get().isDeleted()).isTrue();

        // 자식 댓글 is_deleted 상태 확인
        Optional<Comment> deletedChildComment = commentJpaRepository.findById(childComment.commentId());
        assertThat(deletedChildComment).isPresent();
        assertThat(deletedChildComment.get().isDeleted()).isTrue();

        // 손자 댓글 is_deleted 상태 확인
        Optional<Comment> deletedGrandchildComment = commentJpaRepository.findById(grandchildComment.commentId());
        assertThat(deletedGrandchildComment).isPresent();
        assertThat(deletedGrandchildComment.get().isDeleted()).isTrue();
    }

}