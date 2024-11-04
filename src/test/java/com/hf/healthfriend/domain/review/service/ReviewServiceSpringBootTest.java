package com.hf.healthfriend.domain.review.service;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import com.hf.healthfriend.domain.review.dto.request.ReviewEvaluationDto;
import com.hf.healthfriend.testutil.SampleEntityGenerator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ReviewServiceSpringBootTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReviewService reviewService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    MatchingRepository matchingRepository;

    @DisplayName("멤버 리뷰 별점 업데이트 성공")
    @Test
    void updateMemberReviewScore_success() throws IllegalAccessException {
        Member reviewee = SampleEntityGenerator.generateSampleMember("reviewer@gmail.com", "reviewernick");
        this.memberRepository.save(reviewee);
        for (int i = 1; i<=6; i++) {
            Member reviewer =
                    SampleEntityGenerator.generateSampleMember("sample" + i + "@gmail.com", "nick" + i);
            this.memberRepository.save(reviewer);
            Matching savedMatching = this.matchingRepository.save(
                    new Matching(reviewer,
                            reviewee,
                            "스포애니",
                            "서울시 영등포구",
                            LocalDateTime.now().minusDays(i + 1))
            );
            savedMatching.accept();
            ReviewCreationRequestDto dto = ReviewCreationRequestDto.builder()
                    .reviewerId(reviewer.getId())
                    .revieweeId(reviewee.getId())
                    .score(i % 5 + 1) // Constraint
                    .evaluations(List.of(
                            new ReviewEvaluationDto(EvaluationType.GOOD, 1),
                            new ReviewEvaluationDto(EvaluationType.GOOD, 2),
                            new ReviewEvaluationDto(EvaluationType.NOT_GOOD, 1)
                    ))
                    .matchingId(savedMatching.getMatchingId())
                    .build();
            this.reviewService.addReview(dto);
        }

        entityManager.clear();
        Member updatedReviewee = memberRepository.findById(reviewee.getId()).orElseThrow();
        double actualScore = updatedReviewee.getReviewScore();
        double expectedScore = Math.round(Arrays.stream(new int[] { 1, 2, 3, 4, 5, 1 })
                .average().getAsDouble() * 10.0) / 10.0;
        assertEquals(expectedScore, actualScore, 0.1);
    }
}
