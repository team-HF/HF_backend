package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.querydsl.CommentCustomRepositoryImpl;
import com.hf.healthfriend.domain.like.constant.LikeType;
import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.testutil.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class CommentSortTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentJpaRepository commentRepository;

    @Autowired
    private CommentCustomRepositoryImpl commentCustomRepository;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    private Post testPost;
    private Member testMember;

    @BeforeEach
    public void setUp() {
        testMember = Member.builder()
                .loginId("testUser")
                .role(Role.ROLE_MEMBER)
                .email("test@example.com")
                .password("password123")
                .creationTime(LocalDateTime.now())
                .nickname("TestNickname")
                .profileImageUrl("http://profile.url")
                .birthDate(LocalDate.of(1995, 5, 25))
                .gender(Gender.MALE)
                .introduction("This is a test introduction")
                .fitnessLevel(FitnessLevel.BEGINNER)
                .companionStyle(CompanionStyle.GROUP)
                .fitnessEagerness(FitnessEagerness.EAGER)
                .fitnessObjective(FitnessObjective.RUNNING)
                .fitnessKind(FitnessKind.HIGH_STRESS)
                .posts(new ArrayList<>())  // 빈 리스트 사용
                .specs(new ArrayList<>())  // 빈 리스트 사용
                .build();

        memberRepository.save(testMember);
        testPost = Post.builder()
                .postId(1L)
                .content("content")
                .title("title")
                .member(testMember)
                .build();
        postRepository.save(testPost);

        for(int i=1; i<=5; i++){
            commentRepository.save(Comment.builder()
                            .post(testPost)
                            .commentId((long)i)
                            .content("Comment "+i)
                            .isDeleted(false)
                            .writer(testMember)
                    .build());
        }
        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("댓글 목록을 최신순으로 정렬 ")
    @Test
    void sortByCreationDate(){
        //Given
        Long postId = testPost.getPostId();

        //When
        List<Comment> comments = commentCustomRepository.findCommentsByPostIdWithSorting(postId, CommentSortType.LATEST);

        //Then
        assertThat(comments).hasSize(5);
        assertThat(comments.get(0).getContent()).isEqualTo("Comment 5");
    }

    @DisplayName("댓글 목록을 공감순으로 정렬")
    @Test
    void sortByLikeCount(){
        //Given
        Long postId = testPost.getPostId();
        Comment mostLikedComment = commentRepository.findAll().get(0);
        for (int i = 0; i < 3; i++) {
            //좋아요 3개 추가
            mostLikedComment.getLikes().add(new Like(
                    testMember, mostLikedComment, LikeType.COMMENT));
        }
        commentRepository.save(mostLikedComment);

        //When
        List<Comment> comments = commentCustomRepository.findCommentsByPostIdWithSorting(postId, CommentSortType.MOST_LIKES);

        //Then
        assertThat(comments).hasSize(5);
        assertThat(comments.get(0).getContent()).isEqualTo(mostLikedComment.getContent()); // 좋아요가 가장 많은 댓글이 첫 번째

    }


}
