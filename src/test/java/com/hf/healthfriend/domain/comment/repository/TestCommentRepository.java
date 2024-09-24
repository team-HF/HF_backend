package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({
        "local-dev",
        "secret",
        "constants",
        "priv"
})
class TestCommentRepository {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    Long postWriterId;

    List<Long> commentWriterIds;

    Long postId;

    @BeforeEach
    void beforeEach() {
        // Insert sample members
        Member postWriter = new Member("post@writer.com");
        setNecessaryFieldOfMemberToSampleData(postWriter, "샘플포스터");

        this.postWriterId = this.memberRepository.save(postWriter).getId();

        List<Member> commentWriters = List.of(
                new Member("comment1@writer.com"),
                new Member("comment2@writer.com"),
                new Member("comment3@writer.com")
        );
        commentWriters.forEach((e) -> {
            setNecessaryFieldOfMemberToSampleData(e, e.getLoginId().substring(0, 8));
            this.memberRepository.save(e);
        });

        this.commentWriterIds = commentWriters.stream().map(Member::getId).toList();

        // Insert sample post
        Post post = Post.builder()
                .content("sample-post")
                .title("sample-title")
                .category(PostCategory.COUNSELING)
                .member(postWriter)
                .build();

        this.postRepository.save(post);

        this.postId = post.getPostId();
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

    @DisplayName("findById - 삭제된 레코드는 가져오지 않음")
    @Test
    void findById_ignoreDeleted() {
        Comment comment =
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(0)), "content1");
        Long generatedId = this.commentRepository.save(comment).getCommentId();
        this.commentRepository.deleteById(generatedId);
        assertThat(this.commentRepository.findById(generatedId)).isEmpty();
    }

    @DisplayName("findCommentsByPostId - 다 찾아옴")
    @Test
    void findCommentsByPostId_findAll() {
        // Given
        List<Comment> commentsToInsert = List.of(
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(0)), "content1"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(1)), "content2"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(2)), "content3")
        );

        commentsToInsert.forEach((c) -> this.commentRepository.save(c));

        // When
        long postIdToQuery = this.postId;

        // Then
        List<Comment> result = this.commentRepository.findCommentsByPostId(postIdToQuery);

        log.info("result={}", result);

        assertThat(result).size().isEqualTo(3);
        assertThat(result.stream().map((c) -> c.getWriter().getId()).toList()).containsExactlyInAnyOrder(
                this.commentWriterIds.toArray(Long[]::new)
        );

        result.forEach((c) -> assertThat(c.getPost().getPostId()).isEqualTo(this.postId));
    }

    static Stream<Arguments> findCommentsByPostId_excludeDeleted() {
        // Arguments의 첫 번째 값은 this.commentRepository.findCommentsByPostId 메소드의
        // size 예상값
        // 두 번째 이후의 값은 삭제할 Comment의 indices
        return Stream.of(
                Arguments.of(2, new int[] { 0 }),
                Arguments.of(2, new int[] { 1 }),
                Arguments.of(2, new int[] { 2 }),
                Arguments.of(1, new int[] { 0, 2 }),
                Arguments.of(0, new int[] { 0, 1, 2 })
        );
    }

    @DisplayName("findCommentsByPostId - isDeleted = true 인 거 제외하는 거 체크")
    @MethodSource
    @ParameterizedTest
    void findCommentsByPostId_excludeDeleted(int expectedSize, int[] indicesOfCommentsToDelete) {
        // Given
        List<Comment> commentsToInsert = List.of(
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(0)), "content1"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(1)), "content2"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(this.commentWriterIds.get(2)), "content3")
        );

        commentsToInsert.forEach((c) -> this.commentRepository.save(c));

        for (int indexOfCommentToDelete : indicesOfCommentsToDelete) {
            this.commentRepository.deleteById(commentsToInsert.get(indexOfCommentToDelete).getCommentId());
        }

        // Then
        List<Comment> result = this.commentRepository.findCommentsByPostId(this.postId);

        log.info("result={}", result);

        assertThat(result).size().isEqualTo(expectedSize);
    }

    @DisplayName("findCommentsByWriterId - 다 찾아옴")
    @Test
    void findCommentsByWriterId_findAll() {
        // Given
        long commentWriterId = this.commentWriterIds.get(0);
        List<Comment> commentsToInsert = List.of(
                new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterId), "content1"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterId), "content2"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterId), "content3")
        );

        commentsToInsert.forEach((c) -> this.commentRepository.save(c));

        // When
        long writerIdToQuery = commentWriterId;

        // Then
        List<Comment> result = this.commentRepository.findCommentsByWriterId(writerIdToQuery);

        log.info("result={}", result);

        assertThat(result).size().isEqualTo(3);
    }

    static Stream<Arguments> findCommentsByWriterId_excludeDeleted() {
        // Arguments의 첫 번째 값은 this.commentRepository.findCommentsByPostId 메소드의
        // size 예상값
        // 두 번째 이후의 값은 삭제할 Comment의 indices
        return Stream.of(
                Arguments.of(2, new int[] { 0 }),
                Arguments.of(2, new int[] { 1 }),
                Arguments.of(2, new int[] { 2 }),
                Arguments.of(1, new int[] { 0, 2 }),
                Arguments.of(0, new int[] { 0, 1, 2 })
        );
    }

    @DisplayName("findCommentsByWriterId - isDeleted = true 인 거 제외하는 거 체크")
    @MethodSource
    @ParameterizedTest
    void findCommentsByWriterId_excludeDeleted(int expectedSize, int[] indicesOfCommentsToDelete) {
        // Given
        long commentWriterId = this.commentWriterIds.get(0);
        List<Comment> commentsToInsert = List.of(
                new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterId), "content1"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterId), "content2"),
                new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterId), "content3")
        );

        commentsToInsert.forEach((c) -> this.commentRepository.save(c));

        for (int indexOfCommentToDelete : indicesOfCommentsToDelete) {
            this.commentRepository.deleteById(commentsToInsert.get(indexOfCommentToDelete).getCommentId());
        }

        // Then
        List<Comment> result = this.commentRepository.findCommentsByWriterId(this.commentWriterIds.get(0));

        log.info("result={}", result);

        assertThat(result).size().isEqualTo(expectedSize);
    }

    @DisplayName("updateComment - 수정 성공")
    @Test
    void updateComment_success() {
        Comment comment = new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterIds.get(0)), "previous-content");

        this.commentRepository.save(comment);

        log.info("Before update: {}", comment);

        LocalDateTime record = LocalDateTime.now();
        Comment result =
                this.commentRepository.updateComment(comment.getCommentId(), CommentUpdateDto.builder().content("New Content").build());

        assertThat(result.getContent()).isEqualTo("New Content");
        assertThat(result.getLastModified()).isAfterOrEqualTo(record);
    }

    @DisplayName("updateComment - 수정에는 성공했지만 Update DTO의 null은 반영 안 됨")
    @Test
    void updateComment_success_butNullNotReflected() {
        Comment comment = new Comment(Post.builder().postId(this.postId).build(), new Member(commentWriterIds.get(0)), "previous-content");

        this.commentRepository.save(comment);

        log.info("Before update: {}", comment);

        Comment result =
                this.commentRepository.updateComment(comment.getCommentId(), CommentUpdateDto.builder().content(null).build());

        assertThat(result.getContent()).isEqualTo("previous-content");
    }

    @DisplayName("updateComment - 그런 comment 없어서 수정 실패")
    @Test
    void updateComment_fail_sinceCommentNotFoundException() {
        assertThatExceptionOfType(CommentNotFoundException.class)
                .isThrownBy(() -> this.commentRepository.updateComment(11351L, CommentUpdateDto.builder().content("New Content").build()));
    }

    @DisplayName("deleteById - 삭제된 엔티티를 또 삭제하려고 하면 CommentNotFoundException 발생")
    @Test
    void deleteById_failWhenAttemptsToDeleteDeletedComment() {
        Comment comment = new Comment(Post.builder().postId(this.postId).build(),
                new Member(this.commentWriterIds.get(0)),
                "content1");
        Long generatedId = this.commentRepository.save(comment).getCommentId();
        this.commentRepository.deleteById(generatedId);

        assertThatExceptionOfType(CommentNotFoundException.class)
                .isThrownBy(() -> this.commentRepository.deleteById(generatedId));
    }
}