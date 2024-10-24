package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.follow.entity.Follow;
import com.hf.healthfriend.domain.follow.repository.FollowRepository;
import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.querydsl.MemberCustomRepositoryImpl;
import com.hf.healthfriend.testutil.TestConfig;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
public class MemberCustomRepositoryImlTest {

    @Autowired
    private MemberCustomRepositoryImpl memberCustomRepository;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MembersRecommendRequest request;

    @BeforeEach
    public void setUp(){
        request = MembersRecommendRequest.builder()
                .companionStyleList(List.of(CompanionStyle.GROUP))
                .fitnessKindList(List.of(FitnessKind.FUNCTIONAL))
                .fitnessObjectiveList(List.of(FitnessObjective.RUNNING))
                .fitnessEagernessList(null)
                .memberSortType(MemberSortType.MATCHING_COUNT)
                .build();
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Member member = Member.builder()
                    .loginId("virtualUser"+i)
                    .role(Role.ROLE_MEMBER)
                    .name("Virtual User"+ i)
                    .email("virtual@user.com"+i)
                    .password("password")
                    .nickname("VirtualNickname"+(char) ('A'+i))
                    .city("Seoul")
                    .district("Gangnam")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .gender(Gender.MALE)
                    .introduction("This is a virtual member"+ i)
                    .fitnessLevel(FitnessLevel.BEGINNER)
                    .companionStyle(CompanionStyle.GROUP)
                    .fitnessEagerness(FitnessEagerness.EAGER)
                    .fitnessObjective(FitnessObjective.BULK_UP)
                    .fitnessKind(FitnessKind.HIGH_STRESS)
                    .build();
            memberRepository.save(member);
            members.add(member);
        }
        for(int i = 0; i<=18; i++){
            Follow follow = Follow.builder()
                    .followee(members.get(i))
                    .follower(members.get(i+1))
                    .build();
            followRepository.save(follow);
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("enumToList test")
    public void enumToListTest() {
        List<String> fitnessTypeList = memberCustomRepository.fitnessTypesToList(request);
        List<String> expectedList = List.of("GROUP","FUNCTIONAL","RUNNING");
        assertEquals(expectedList, fitnessTypeList);
    }

    @Test
    @DisplayName("filtering test")
    public void filteringTest() {
        // Given
        BooleanBuilder builder = memberCustomRepository.filter(request);
        // When
        Predicate predicate = builder.getValue();
        // Then
        assertThat(predicate.toString()).contains("member1.companionStyle = GROUP");
        assertThat(predicate.toString()).contains("member1.fitnessKind = FUNCTIONAL");
        assertThat(predicate.toString()).contains("member1.fitnessObjective = RUNNING");
        assertThat(predicate.toString()).doesNotContain("member1.fitnessEagerness = EAGER");
    }

    @Test
    @DisplayName("profile search test")
    public void profileSearchTest() {
        //Given
        String keyword = "VirtualNicknameB";
        Pageable pageable = PageRequest.of(0, 10);

        //When
        List<MemberSearchResponse> searchedProfileList = memberCustomRepository.searchMembers(keyword, pageable);

        //Then
        assertEquals(1, searchedProfileList.size());
        assertEquals("This is a virtual member1",searchedProfileList.get(0).getIntroduction());
        assertEquals(1,searchedProfileList.get(0).getFollowerCount());

    }

}
