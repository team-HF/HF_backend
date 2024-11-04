package com.hf.healthfriend.domain.review.service;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
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
import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.entity.ReviewEvaluation;
import com.hf.healthfriend.domain.review.repository.ReviewRepository;
import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TestReviewService {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    MatchingRepository matchingRepository;

    @Mock
    MemberRepository memberRepository;

    Map<Long, Member> dummyMembers;
    Map<Long, Matching> dummyMatchings;

    @BeforeEach
    void beforeEach() throws IllegalAccessException, NoSuchFieldException {
        Member member1 = SampleEntityGenerator.generateSampleMember("sample1@gmail.com", "nick1");
        member1.setId(1000L);
        Member member2 = SampleEntityGenerator.generateSampleMember("sample2@gmail.com", "nick2");
        member2.setId(1001L);
        member1.setFitnessLevel(FitnessLevel.ADVANCED);
        member2.setFitnessLevel(FitnessLevel.BEGINNER);

        Matching matching = new Matching(member1, member2, "스포애니", "서울시 영등포구 당산역", LocalDateTime.now().plusDays(1));
        Field idField = Arrays.stream(Matching.class.getDeclaredFields()).filter((field) -> field.isAnnotationPresent(Id.class)).findAny().get();
        Field meetingTimeField = Matching.class.getDeclaredField("meetingTime");
        idField.setAccessible(true);
        idField.set(matching, 10002L);
        idField.setAccessible(false);
        meetingTimeField.setAccessible(true);
        meetingTimeField.set(matching, LocalDateTime.now().minusDays(1));
        meetingTimeField.setAccessible(false);

        this.dummyMembers = Map.of(
                member1.getId(), member1,
                member2.getId(), member2
        );
        this.dummyMatchings = Map.of(
                matching.getMatchingId(), matching
        );
    }

    @DisplayName("addReview - success")
    @Test
    void addReview_success() throws NoSuchFieldException, IllegalAccessException {
        // Given
        Member reviewer = this.dummyMembers.get(1000L);
        Member reviewee = this.dummyMembers.get(1001L);
        Matching matching = this.dummyMatchings.get(10002L);
        matching.accept();

        ReviewCreationRequestDto requestDto = ReviewCreationRequestDto.builder()
                .matchingId(matching.getMatchingId())
                .reviewerId(reviewer.getId())
                .revieweeId(reviewee.getId())
                .score(3)
                .evaluations(SampleEntityGenerator.generateSampleReviewEvaluation()
                        .stream()
                        .map((entity) -> new ReviewEvaluationDto(entity.getEvaluationType(), entity.getEvaluationDetailId()))
                        .toList())
                .build();

        Review mockReview = new Review(
                reviewer,
                reviewee,
                matching,
                requestDto.getScore(),
                requestDto.getEvaluations()
                        .stream()
                        .map((e) -> new ReviewEvaluation(e.getEvaluationType(), e.getEvaluationDetailId()))
                        .toList()
        );
        Field reviewIdField = Review.class.getDeclaredField("reviewId");
        reviewIdField.setAccessible(true);
        reviewIdField.set(mockReview, 2000L);
        reviewIdField.setAccessible(false);
        when(this.reviewRepository.save(any()))
                .thenReturn(mockReview);
        when(this.matchingRepository.findById(matching.getMatchingId()))
                .thenReturn(Optional.of(matching));

        // When
        Long autogeneratedId = this.reviewService.addReview(requestDto);

        assertThat(autogeneratedId).isNotNull();
        assertThat(matching.getStatus()).isEqualTo(MatchingStatus.FINISHED);
    }

    @DisplayName("addReview - reviewer가 없는 회원일 경우 MemberNotFoundException 발생")
    @Test
    void addReview_expectedMemberNotFoundException() {
        Matching matching = this.dummyMatchings.get(10002L);
        matching.accept();
        when(this.matchingRepository.findById(10002L)).thenReturn(Optional.of(matching));
        when(this.reviewRepository.save(any())).thenThrow(new DataIntegrityViolationException("Referential integrity violation"));

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() ->
                        this.reviewService.addReview(ReviewCreationRequestDto.builder()
                                .reviewerId(1000L)
                                .revieweeId(1001L)
                                .score(3)
                                .evaluations(List.of(
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                                        new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 1)
                                ))
                                .matchingId(matching.getMatchingId())
                                .build()));
    }

    @DisplayName("addReview - 없는 매칭에 대해 리뷰를 남길 경우 MatchingNotFoundException 발생")
    @Test
    void addReview_expectedMatchingNotFoundException() {
        when(this.matchingRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(MatchingNotFoundException.class)
                .isThrownBy(() ->
                        this.reviewService.addReview(ReviewCreationRequestDto.builder()
                                .reviewerId(1000L)
                                .revieweeId(1001L)
                                .score(3)
                                .evaluations(List.of(
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                                        new ReviewEvaluationDto(EvaluationType.GOOD, 3)
                                ))
                                .matchingId(10002L)
                                .build()));
    }

    @DisplayName("getRevieweeInfo - success")
    @Test
    void getRevieweeInfo_success() {
        // Given
        Member reviewee = this.dummyMembers.get(1000L);
        when(this.memberRepository.existsById(reviewee.getId())).thenReturn(true);
        when(this.reviewRepository.getRevieweeStatistics(reviewee.getId()))
                .thenReturn(List.of(
                        new RevieweeStatisticsMapping(
                                EvaluationType.GOOD, 1, 2L
                        ),
                        new RevieweeStatisticsMapping(
                                EvaluationType.GOOD, 2, 1L
                        ),
                        new RevieweeStatisticsMapping(
                                EvaluationType.GOOD, 1, 2L
                        ),
                        new RevieweeStatisticsMapping(
                                EvaluationType.GOOD, 2, 1L
                        )
                ));
        when(this.reviewRepository.calculateAverageScoreByRevieweeId(reviewee.getId()))
                .thenReturn(3.5);

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
        RevieweeResponseDto result = this.reviewService.getRevieweeInfo(reviewee.getId());

        // Then
        log.info("result={}", result);

        assertThat(result.averageScore()).isEqualTo(3.5);
        assertThat(result.memberId()).isEqualTo(reviewee.getId());
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