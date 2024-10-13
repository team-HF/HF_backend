package com.hf.healthfriend.domain.review.service;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import com.hf.healthfriend.domain.review.dto.request.ReviewEvaluationDto;
import com.hf.healthfriend.domain.review.dto.response.RevieweeResponseDto;
import com.hf.healthfriend.domain.review.repository.ReviewRepository;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@ActiveProfiles({
        "local-dev",
        "secret",
        "priv",
        "constants"
})
@SpringBootTest
@Transactional
class TestReviewService {

    ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MatchingRepository matchingRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        this.reviewService = new ReviewService(this.reviewRepository, this.matchingRepository, this.memberRepository);
    }

    @DisplayName("addReview - success")
    @Test
    void addReview_success() {
        Object[] dummyData = generateDummyData();
        Member member1 = (Member) dummyData[0];
        Member member2 = (Member) dummyData[1];
        Matching savedMatching = (Matching) dummyData[2];

        Long autogeneratedId = this.reviewService.addReview(ReviewCreationRequestDto.builder()
                .reviewerId(member2.getId())
                .revieweeId(member1.getId())
                .score(3)
                .evaluations(List.of(
                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                        new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                        new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 1)
                ))
                .matchingId(savedMatching.getMatchingId())
                .build());
        assertThat(autogeneratedId).isNotNull();
    }

    @DisplayName("addReview - reviewer가 없는 회원일 경우 MemberNotFoundException 발생")
    @Test
    void addReview_expectedMemberNotFoundException() {
        Object[] dummyData = generateDummyData();
        Member member1 = (Member) dummyData[0];
        Member member2 = (Member) dummyData[1];
        Matching savedMatching = (Matching) dummyData[2];

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() ->
                        this.reviewService.addReview(ReviewCreationRequestDto.builder()
                                .reviewerId(member2.getId() + 1)
                                .revieweeId(member1.getId() + 1)
                                .score(3)
                                .evaluations(List.of(
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                                        new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 1)
                                ))
                                .matchingId(savedMatching.getMatchingId())
                                .build()));
    }

    @DisplayName("addReview - 없는 매칭에 대해 리뷰를 남길 경우 MatchingNotFoundException 발생")
    @Test
    void addReview_expectedMatchingNotFoundException() {
        Object[] dummyData = generateDummyData();
        Member member1 = (Member) dummyData[0];
        Matching savedMatching = (Matching) dummyData[2];

        assertThatExceptionOfType(MatchingNotFoundException.class)
                .isThrownBy(() ->
                        this.reviewService.addReview(ReviewCreationRequestDto.builder()
                                .reviewerId(member1.getId() + 1)
                                .score(3)
                                .evaluations(List.of(
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 3)
                                ))
                                .matchingId(savedMatching.getMatchingId() + 1)
                                .build()));
    }

    private Object[] generateDummyData() {
        Member member1 = SampleEntityGenerator.generateSampleMember("sample1@gmail.com", "nick1");
        Member member2 = SampleEntityGenerator.generateSampleMember("sample2@gmail.com", "nick2");
        member1.setFitnessLevel(FitnessLevel.ADVANCED);
        member2.setFitnessLevel(FitnessLevel.BEGINNER);
        this.memberRepository.save(member1);
        this.memberRepository.save(member2);

        Matching savedMatching = this.matchingRepository.save(new Matching(member1, member2));
        return new Object[]{
                member1, member2, savedMatching
        };
    }

    @DisplayName("getRevieweeInfo - success")
    @Test
    void getRevieweeInfo_success() {
        // Given
        Member beginner1 = SampleEntityGenerator.generateSampleMember("beginner1@gmail.com", "nick1");
        beginner1.setFitnessLevel(FitnessLevel.BEGINNER);
        Member beginner2 = SampleEntityGenerator.generateSampleMember("beginner2@gmail.com", "nick2");
        beginner2.setFitnessLevel(FitnessLevel.BEGINNER);
        Member advanced = SampleEntityGenerator.generateSampleMember("advanced@gmail.com", "nick3");
        advanced.setFitnessLevel(FitnessLevel.ADVANCED);

        beginner1 = this.memberRepository.save(beginner1);
        beginner2 = this.memberRepository.save(beginner2);
        advanced = this.memberRepository.save(advanced);

        Matching matching1 = new Matching(beginner1, advanced);
        Matching matching2 = new Matching(beginner2, advanced);
        matching1 = this.matchingRepository.save(matching1);
        matching2 = this.matchingRepository.save(matching2);

        ReviewCreationRequestDto requestDto1 = ReviewCreationRequestDto.builder()
                .reviewerId(beginner1.getId())
                .revieweeId(advanced.getId())
                .matchingId(matching1.getMatchingId())
                .score(3)
                .evaluations(List.of(
                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                        new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                        new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 1)
                ))
                .build();
        ReviewCreationRequestDto requestDto2 = ReviewCreationRequestDto.builder()
                .reviewerId(beginner2.getId())
                .revieweeId(advanced.getId())
                .matchingId(matching2.getMatchingId())
                .score(4)
                .evaluations(List.of(
                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                        new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 1),
                        new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 2)
                ))
                .build();

        this.reviewService.addReview(requestDto1);
        this.reviewService.addReview(requestDto2);

        Map<EvaluationType, Map<Integer, Long>> expectedMap = Map.of(
                EvaluationType.GOOD,
                Map.of(
                        1, 2L,
                        2, 1L
                ),
                EvaluationType.NOT_GOOD,
                Map.of(
                        1, 2L,
                        2, 1L
                )
        );

        // When
        RevieweeResponseDto result = this.reviewService.getRevieweeInfo(advanced.getId());

        // Then
        log.info("result={}", result);

        assertThat(result.averageScore()).isEqualTo(3.5);
        assertThat(result.memberId()).isEqualTo(advanced.getId());
        result.reviewDetails().forEach((re) -> {
            assertThat(re.totalCountPerEvaluationType()).isEqualTo(3L);
            Map<Integer, Long> counts = expectedMap.get(re.evaluationType());
            assertThat(counts).isNotNull();
            counts.forEach((k, v) -> {
                Long expectedCount = counts.get(k);
                assertThat(expectedCount).isNotNull();
                assertThat(expectedCount).isEqualTo(v);
            });
        });
    }
}