package com.hf.healthfriend.domain.member.repository;

import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.repository.querydsl.MemberCustomRepositoryImpl;
import com.hf.healthfriend.testutil.TestConfig;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class MemberCustomRepositoryImlTest {

    @Mock
    private JPAQueryFactory queryFactory;

    @InjectMocks
    private MemberCustomRepositoryImpl memberCustomRepository;

    private MembersRecommendRequest request;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        request = MembersRecommendRequest.builder()
                .companionStyleList(List.of(CompanionStyle.GROUP))
                .fitnessKindList(List.of(FitnessKind.FUNCTIONAL))
                .fitnessObjectiveList(List.of(FitnessObjective.RUNNING))
                .fitnessEagernessList(null)
                .memberSortType(MemberSortType.MATCHING_COUNT)
                .build();
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

}
