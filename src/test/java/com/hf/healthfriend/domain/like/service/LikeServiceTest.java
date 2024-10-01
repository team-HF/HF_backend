package com.hf.healthfriend.domain.like.service;

import com.hf.healthfriend.domain.like.dto.PostLikeDto;
import com.hf.healthfriend.domain.like.exception.DuplicateLikeException;
import com.hf.healthfriend.domain.like.exception.PostOrMemberNotExistsException;
import com.hf.healthfriend.domain.like.repository.LikeRepository;
import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles({
        "local-dev",
        "secret",
        "priv",
        "constants"
})
@Transactional
class LikeServiceTest {

    @Autowired
    LikeService likeService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    Map<String, Member> sampleMembers;

    Map<String, Post> samplePosts;

    @BeforeEach
    void beforeEach() {
        this.sampleMembers = Map.of(
                "member1", generateSampleMember("sample1@gmail.com", "nick1")
        );

        this.samplePosts = Map.of(
                "post1", generateSamplePost(this.sampleMembers.get("member1"))
        );

        this.sampleMembers.values().forEach(this.memberRepository::save);
        this.postRepository.saveAll(this.samplePosts.values());
    }

    private Member generateSampleMember(String email, String nickname) {
        Member member = new Member(email);
        member.setNickname(nickname);
        member.setBirthDate(LocalDate.of(1997, 9, 16));
        member.setGender(Gender.MALE);
        member.setIntroduction("");
        member.setFitnessLevel(FitnessLevel.BEGINNER);
        member.setCompanionStyle(CompanionStyle.GROUP);
        member.setFitnessEagerness(FitnessEagerness.EAGER);
        member.setFitnessObjective(FitnessObjective.BULK_UP);
        member.setFitnessKind(FitnessKind.FUNCTIONAL);
        return member;
    }

    private Post generateSamplePost(Member member) {
        return Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.COUNSELING)
                .member(member)
                .build();
    }

    @DisplayName("addLike - successfully save")
    @Test
    void addLike_success() {
        assertThatNoException()
                .isThrownBy(() -> this.likeService.addLike(
                        this.sampleMembers.get("member1").getId(),
                        this.samplePosts.get("post1").getPostId()
                ));
    }

    @DisplayName("addLike - 좋아요 취소 후 다시 addLike 호출")
    @Test
    void addLike_againAfterCancel() {
        Long generatedId = this.likeService.addLike(
                this.sampleMembers.get("member1").getId(),
                this.samplePosts.get("post1").getPostId()
        );

        this.likeService.cancelLike(generatedId);

        try {
            Long likeId = this.likeService.addLike(
                    this.sampleMembers.get("member1").getId(),
                    this.samplePosts.get("post1").getPostId()
            );

            assertThat(likeId).isEqualTo(generatedId);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @DisplayName("addLike - 중복 좋아요 시 예외 발생 - DuplicateLikeException")
    @Test
    void addLike_fail() {
        assertThatNoException()
                .isThrownBy(() -> this.likeService.addLike(
                        this.sampleMembers.get("member1").getId(),
                        this.samplePosts.get("post1").getPostId()
                ));

        assertThatExceptionOfType(DuplicateLikeException.class)
                .isThrownBy(() -> this.likeService.addLike(
                        this.sampleMembers.get("member1").getId(),
                        this.samplePosts.get("post1").getPostId()
                ));
    }

    @DisplayName("addLike - 존재하지 않는 Post나 Member와 연관관계 매핑을 가질 경우 예외 발생")
    @CsvSource(value = {
            "0:1", "1:0", "1:1"
    }, delimiter = ':')
    @ParameterizedTest
    void addLike_fail_postOfPostIdOrMemberOfMemberIdNotExists(int memberIdIncr, int postIdIncr) {
        Long memberId = this.sampleMembers.get("member1").getId();
        Long postId = this.samplePosts.get("post1").getPostId();
        assertThatExceptionOfType(PostOrMemberNotExistsException.class)
                .isThrownBy(() -> this.likeService.addLike(memberId + memberIdIncr, postId + + postIdIncr));
    }

    @DisplayName("getLike - success")
    @Test
    void getLike_success() {
        Long autogeneratedLikeId = this.likeService.addLike(
                this.sampleMembers.get("member1").getId(),
                this.samplePosts.get("post1").getPostId()
        );

        PostLikeDto result = this.likeService.getLike(autogeneratedLikeId);
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(this.sampleMembers.get("member1").getId());
        assertThat(result.getPostId()).isEqualTo(this.samplePosts.get("post1").getPostId());
    }

    @DisplayName("getLike - 존재하지 않는 Like를 찾으려 할 경우 예외 발생 - NoSuchElementException")
    @Test
    void getLike_noSuchElementException() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> this.likeService.getLike(13513421L))
                .withMessage("존재하지 않는 Like입니다. ID=%d", 13513421L); // TODO: 하드코딩
    }

    @DisplayName("getLikeOfPost - success")
    @Test
    void getLikeOfPost_success() {
        PostLikeDto postLikeDto = new PostLikeDto(
                this.sampleMembers.get("member1").getId(),
                this.samplePosts.get("post1").getPostId()
        );

        this.likeService.addLike(
                this.sampleMembers.get("member1").getId(),
                this.samplePosts.get("post1").getPostId()
        );

        List<PostLikeDto> result = this.likeService.getLikeOfPost(this.samplePosts.get("post1").getPostId());

        assertThat(result).size().isEqualTo(1);
        assertThat(result.get(0).getPostId()).isEqualTo(this.samplePosts.get("post1").getPostId());
    }

    @DisplayName("getLikeOfMember - success")
    @Test
    void getLikeOfMember_success() {
        this.likeService.addLike(
                this.sampleMembers.get("member1").getId(),
                this.samplePosts.get("post1").getPostId()
        );

        List<PostLikeDto> result = this.likeService.getLikeOfMember(this.sampleMembers.get("member1").getId());

        assertThat(result).size().isEqualTo(1);
        assertThat(result.get(0).getMemberId()).isEqualTo(this.sampleMembers.get("member1").getId());
    }

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    DataSource dataSource;

    @DisplayName("cancelLike - 취소된 좋아요는 가져올 수 없음")
    @Test
    void cancelLike_success() throws SQLException {
        Long generatedId = this.likeService.addLike(
                this.sampleMembers.get("member1").getId(),
                this.samplePosts.get("post1").getPostId()
        );

        PostLikeDto like = this.likeService.getLike(generatedId);
        assertThat(like).isNotNull();

        this.likeService.cancelLike(generatedId);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> this.likeService.getLike(generatedId));

        this.likeRepository.flush();

        // 실제 DB에서 레코드가 삭제된 게 아니라 is_canceled가 true로 세팅된 것 확인

        Connection connection = DataSourceUtils.getConnection(this.dataSource);
        PreparedStatement stmt = connection.prepareStatement("""
                SELECT is_canceled
                FROM likes
                WHERE like_id = ?
                """);
        stmt.setLong(1, generatedId);
        ResultSet rs = stmt.executeQuery();
        assertThat(rs.next()).isTrue();
        boolean canceled = rs.getBoolean("is_canceled");
        assertThat(canceled).isTrue();

        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DataSourceUtils.releaseConnection(connection, this.dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}