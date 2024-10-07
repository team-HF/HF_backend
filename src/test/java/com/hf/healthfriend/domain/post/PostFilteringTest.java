package com.hf.healthfriend.domain.post;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.domain.post.repository.querydsl.PostCustomRepositoryImpl;
import com.hf.healthfriend.testutil.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class PostFilteringTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostCustomRepositoryImpl postCustomRepository;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 20; i++) {
            Member member = Member.builder()
                    .loginId("virtualUser"+i)
                    .role(Role.ROLE_MEMBER)
                    .name("Virtual User")
                    .email("virtual@user.com"+i)
                    .password("password")
                    .nickname("VirtualNickname"+i)
                    .city("Seoul")
                    .district("Gangnam")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .gender(Gender.MALE)
                    .introduction("This is a virtual member.")
                    .fitnessLevel(FitnessLevel.BEGINNER)
                    .companionStyle(CompanionStyle.GROUP)
                    .fitnessEagerness(FitnessEagerness.EAGER)
                    .fitnessObjective(FitnessObjective.BULK_UP)
                    .fitnessKind(FitnessKind.HIGH_STRESS)
                    .build();
            memberRepository.save(member);
            Post post = Post.builder()
                    .postId((long) i)
                    .title("Post " + i)
                    .category(i % 2 == 0 ? PostCategory.COUNSELING : PostCategory.GYM_RECOMMENDATION)
                    .viewCount((long) (i * 10))
                    .content("Content " + i)
                    .member(member)
                    .build();
            postRepository.save(post);
        }
        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("필터링 결과 확인")
    @Test
    void testGetListWithFilters() {
        // Given
        FitnessLevel fitnessLevel = FitnessLevel.BEGINNER;
        PostCategory postCategory = PostCategory.COUNSELING;
        String keyword = "Content 2";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        List<PostListObject> result = postCustomRepository.getList(fitnessLevel, postCategory, keyword, pageable);

        // Then
        assertEquals(2, result.size());
        assertEquals("Post 20", result.get(0).title());
    }
}
