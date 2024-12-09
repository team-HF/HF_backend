package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.querydsl.MemberCustomRepositoryImpl;
import com.hf.healthfriend.domain.wish.entity.Wish;
import com.hf.healthfriend.domain.wish.repository.WishRepository;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
public class MemberCustomRepositoryImlTest {

    @Autowired
    private MemberCustomRepositoryImpl memberCustomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WishRepository wishRepository;

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
                    .nickname("VirtualNickname"+(char) ('A'+i-1))
                    .cd1("01")
                    .cd2("001")
                    .cd3("003")
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
            Wish wish = new Wish(members.get(i),members.get(i+1));
            wishRepository.save(wish);
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
        assertEquals("This is a virtual member2",searchedProfileList.get(0).getIntroduction());
        assertEquals(1,searchedProfileList.get(0).getFollowerCount());

    }

    @DisplayName("findByMemberId - success")
    @Test
    void findByMemberId_success() {
        // Given
        Member sampleMember = SampleEntityGenerator.generateSampleMember("sample@gmail.com", "samplenickname");
        this.memberRepository.save(sampleMember);
        this.entityManager.flush();
        this.entityManager.clear();

        // When
        Optional<Member> memberOp = this.memberCustomRepository.findByMemberId(sampleMember.getId());

        // Then
        assertThat(memberOp).isNotEmpty();

        Member member = memberOp.get();

        System.out.println("findMember ID=" + member.getId());
        System.out.println("findMember Email=" + member.getEmail());
        System.out.println("findMember Nickname=" + member.getNickname());

        assertThat(member.getId()).isEqualTo(sampleMember.getId());
        assertThat(member.getEmail()).isEqualTo(sampleMember.getEmail());
        assertThat(member.getNickname()).isEqualTo(sampleMember.getNickname());
    }
}
