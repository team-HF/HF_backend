package com.hf.healthfriend.domain.like.repository;

import com.hf.healthfriend.domain.like.constant.LikeType;
import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles({
        "local-dev",
        "secret",
        "priv",
        "constants"
})
@Transactional
class LikeRepositoryTest {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    Set<Long> sampleMemberIds;

    Member member1;
    Member member2;
    Member member3;
    Post savedPost1;
    Post savedPost2;
    Like likeMember1_Post1;
    Like likeMember2_Post1;
    Like likeMember3_Post1;
    Like likeMember3_Post2;

    @BeforeEach
    void beforeEach() {
        this.member1 = SampleEntityGenerator.generateSampleMember("sample1@gmail.com", "nick1");
        this.member2 = SampleEntityGenerator.generateSampleMember("sample2@gmail.com", "nick2");
        this.member3 = SampleEntityGenerator.generateSampleMember("sample3@gmail.com", "nick3");

        this.memberRepository.save(member1);
        this.memberRepository.save(member2);
        this.memberRepository.save(member3);

        this.sampleMemberIds = Set.of(
                member1.getId(),
                member2.getId(),
                member3.getId()
        );

        this.savedPost1 = this.postRepository.save(generateSamplePost(member1));
        this.savedPost2 = this.postRepository.save(generateSamplePost(member2));

        this.likeMember1_Post1 = this.likeRepository.save(new Like(member1, this.savedPost1, LikeType.POST));
        this.likeMember2_Post1 = this.likeRepository.save(new Like(member2, this.savedPost1,LikeType.POST));
        this.likeMember3_Post1 = this.likeRepository.save(new Like(member3, this.savedPost1,LikeType.POST));
        this.likeMember3_Post2 = this.likeRepository.save(new Like(member3, this.savedPost2,LikeType.POST));
    }

    private Post generateSamplePost(Member member) {
        return Post.builder()
                .title("sample-post")
                .content("sample-content")
                .category(PostCategory.COUNSELING)
                .member(member)
                .build();
    }

    @DisplayName("findByMemberId - 결과가 있음")
    @Test
    void findByMemberId_hasResultMoreThanOne() {
        List<Like> result = this.likeRepository.findPostLikeByMemberId(member1.getId());
        assertThat(result).size().isEqualTo(1);
        assertThat(result.stream().map(Like::getLikeId).toList()).containsExactly(this.likeMember1_Post1.getLikeId());

        List<Like> result2 = this.likeRepository.findPostLikeByMemberId(member3.getId());
        assertThat(result2).size().isEqualTo(2);
        assertThat(result2.stream().map(Like::getLikeId).toList())
                .containsExactlyInAnyOrder(this.likeMember3_Post1.getLikeId(), this.likeMember3_Post2.getLikeId());
    }

    @DisplayName("findByMemberId - cancel된 Like는 가져오지 않음")
    @Test
    void findByMemberId_ignoreCanceledLike() {
        this.likeMember3_Post1.cancel();

        List<Like> result = this.likeRepository.findPostLikeByMemberId(this.member3.getId());
        assertThat(result).size().isEqualTo(1);
        assertThat(result.stream().map(Like::getLikeId).toList())
                .doesNotContain(likeMember3_Post1.getLikeId());
        assertThat(result.stream().map(Like::getLikeId).toList())
                .containsExactly(likeMember3_Post2.getLikeId());
    }

    @DisplayName("findByPostId")
    @Test
    void findByPostId() {
        List<Like> result = this.likeRepository.findByPostId(this.savedPost1.getPostId());
        assertThat(result).size().isEqualTo(3);
        assertThat(result.stream().map(Like::getLikeId).toList())
                .containsExactlyInAnyOrder(
                        this.likeMember1_Post1.getLikeId(),
                        this.likeMember2_Post1.getLikeId(),
                        this.likeMember3_Post1.getLikeId()
                );
    }

    @DisplayName("findByPostId - cancel된 Like는 가져오지 않음")
    @Test
    void findByPostId_ignoreCanceledLikes() {
        this.likeMember2_Post1.cancel();

        List<Like> result = this.likeRepository.findByPostId(this.savedPost1.getPostId());

        assertThat(result).size().isEqualTo(2);
        assertThat(result.stream().map(Like::getLikeId).toList())
                .doesNotContain(this.likeMember2_Post1.getLikeId());
        assertThat(result.stream().map(Like::getLikeId).toList())
                .containsExactlyInAnyOrder(this.likeMember1_Post1.getLikeId(),
                        this.likeMember3_Post1.getLikeId());
    }

    @DisplayName("countByPostId")
    @Test
    void countByPostId() {
        Long result = this.likeRepository.countByPostId(this.savedPost1.getPostId());
        assertThat(result).isEqualTo(3);
    }

    @DisplayName("countByPostId - 취소된 좋아요는 무시")
    @Test
    void countByPostId_ignoreCanceledLikes() {
        this.likeMember2_Post1.cancel();

        Long result = this.likeRepository.countByPostId(this.savedPost1.getPostId());

        assertThat(result).isEqualTo(2);
    }

    @DisplayName("existsByPostAndMember - exists and return true")
    @Test
    void existsByPostAndMember_exists() {
        Post post = Post.builder()
                .postId(this.savedPost1.getPostId())
                .build();
        Member member = new Member(this.member1.getId());
        boolean result = this.likeRepository.existsByPostAndMember(post, member);
        assertThat(result).isTrue();
    }

    @DisplayName("existsByPostAndMember - does not exists and return false")
    @Test
    void existsByPostAndMember_doesNotExists() {
        Post post = Post.builder()
                .postId(this.savedPost2.getPostId())
                .build();
        Member member = new Member(this.member1.getId());
        boolean result = this.likeRepository.existsByPostAndMember(post, member);
        assertThat(result).isFalse();
    }
}