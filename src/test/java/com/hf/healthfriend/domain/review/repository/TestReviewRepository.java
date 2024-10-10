package com.hf.healthfriend.domain.review.repository;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.entity.ReviewEvaluation;
import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import com.hf.healthfriend.testutil.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@ActiveProfiles({
        "local-dev",
        "secret",
        "priv",
        "constants"
})
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestReviewRepository {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MemberJpaRepository memberRepository;

    @Autowired
    MatchingRepository matchingRepository;

    Member sampleAdvanced;
    Member sampleBeginner1;
    Member sampleBeginner2;
    Matching sampleMatching1;
    Matching sampleMatching2;

    @BeforeEach
    void beforeEach() {
        Member advanced = SampleEntityGenerator.generateSampleMember("advanced@gmail.com", "nick1");
        advanced.setFitnessLevel(FitnessLevel.ADVANCED);
        Member beginner1 = SampleEntityGenerator.generateSampleMember("beginner1@gmail.com", "nick2");
        beginner1.setFitnessLevel(FitnessLevel.BEGINNER);
        Member beginner2 = SampleEntityGenerator.generateSampleMember("beginner2@gmail.com", "nick3");
        beginner2.setFitnessLevel(FitnessLevel.BEGINNER);
        this.sampleAdvanced = this.memberRepository.save(advanced);
        this.sampleBeginner1 = this.memberRepository.save(beginner1);
        this.sampleBeginner2 = this.memberRepository.save(beginner2);

        Matching matching1 = new Matching(beginner1, advanced);
        this.sampleMatching1 = this.matchingRepository.save(matching1);

        Matching matching2 = new Matching(beginner2, advanced);
        this.sampleMatching2 = this.matchingRepository.save(matching2);
    }

    @DisplayName("findByRevieweeId - success")
    @Test
    void findByRevieweeId_success() {
        // Given
        Review review1 = SampleEntityGenerator.generateSampleReview(this.sampleMatching1, this.sampleBeginner1, List.of(
                new ReviewEvaluation(EvaluationType.GOOD, 1),
                new ReviewEvaluation(EvaluationType.GOOD, 2),
                new ReviewEvaluation(EvaluationType.NOT_GOOD, 1)
        ));
        this.reviewRepository.save(review1);
        Review review2 = SampleEntityGenerator.generateSampleReview(this.sampleMatching2, this.sampleBeginner2, List.of(
                new ReviewEvaluation(EvaluationType.GOOD, 1),
                new ReviewEvaluation(EvaluationType.NOT_GOOD, 1)
        ));
        this.reviewRepository.save(review2);

        // When
        List<RevieweeStatisticsMapping> result = this.reviewRepository.getRevieweeStatistics(this.sampleAdvanced.getId());

        // log for debug
        for (RevieweeStatisticsMapping mapping : result) {
            log.info("mapping.getEvaluationType={}, mapping.getEvaluationDetailId={}, mapping.getEvaluationDetailCount={}",
                    mapping.getEvaluationType(), mapping.getEvaluationDetailId(), mapping.getEvaluationDetailCount());
            log.info("evaluationType={}", mapping.getEvaluationType());
        }

        // Then
        List<RevieweeStatisticsMapping> onlyGood = result.stream()
                .filter((r) -> r.getEvaluationType() == EvaluationType.GOOD)
                .toList();
        assertThat(onlyGood.get(0).getEvaluationDetailId()).isEqualTo(1);
        assertThat(onlyGood.get(0).getEvaluationDetailCount()).isEqualTo(2);

        assertThat(onlyGood.get(1).getEvaluationDetailId()).isEqualTo(2);
        assertThat(onlyGood.get(1).getEvaluationDetailCount()).isEqualTo(1);

        List<RevieweeStatisticsMapping> onlyNotGood = result.stream()
                .filter((r) -> r.getEvaluationType() == EvaluationType.NOT_GOOD)
                .toList();
        assertThat(onlyNotGood).size().isEqualTo(1);
        assertThat(onlyNotGood.get(0).getEvaluationDetailId()).isEqualTo(1);
        assertThat(onlyNotGood.get(0).getEvaluationDetailCount()).isEqualTo(2);
    }

    @DisplayName("calculateAverageScoreByRevieweeId - success")
    @Test
    void calculateAverageScoreByRevieweeId_success() {
        // Given
        Review review1 = SampleEntityGenerator.generateSampleReview(this.sampleMatching1, this.sampleBeginner1, 3);
        review1 = this.reviewRepository.save(review1);
        Review review2 = SampleEntityGenerator.generateSampleReview(this.sampleMatching2, this.sampleBeginner2, 4);
        review2 = this.reviewRepository.save(review2);

        // When
        double result = this.reviewRepository.calculateAverageScoreByRevieweeId(this.sampleAdvanced.getId());

        log.info("result={}", result);

        // Then
        assertThat(result).isEqualTo(((double)review1.getScore() + review2.getScore()) / 2);
    }
}