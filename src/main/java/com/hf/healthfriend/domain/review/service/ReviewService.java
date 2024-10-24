package com.hf.healthfriend.domain.review.service;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import com.hf.healthfriend.domain.review.dto.request.ReviewEvaluationDto;
import com.hf.healthfriend.domain.review.dto.response.ReviewDetailPerEvaluationType;
import com.hf.healthfriend.domain.review.dto.response.ReviewResponseDto;
import com.hf.healthfriend.domain.review.dto.response.RevieweeResponseDto;
import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.entity.ReviewEvaluation;
import com.hf.healthfriend.domain.review.exception.DuplicateReviewException;
import com.hf.healthfriend.domain.review.exception.InvalidEvaluationsException;
import com.hf.healthfriend.domain.review.exception.ReviewBeforeMeetingException;
import com.hf.healthfriend.domain.review.repository.ReviewRepository;
import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;

    /**
     * 리뷰를 추가한다.
     *
     * @param dto 리뷰 정보가 담긴 DTO
     * @return 자동 생성된 리뷰의 ID
     */
    public Long addReview(ReviewCreationRequestDto dto) {
        // TODO: 동시성 문제 해결해야 함
        // 한 번에 여러 리뷰를 등록하려고 할 경우 동시성 문제 발생 - 분산 락으로 해결해야 함
        Matching targetMatching = fetchMatchingAndValidate(dto);

        Review newReview = new Review(
                new Member(dto.getReviewerId()),
                new Member(dto.getRevieweeId()),
                targetMatching,
                dto.getScore(),
                dto.getEvaluations() == null
                        ? List.of()
                        : dto.getEvaluations()
                        .stream()
                        .map((e) -> new ReviewEvaluation(e.getEvaluationType(), e.getEvaluationDetailId()))
                        .toList()
        );

        try {
            Review saved = this.reviewRepository.save(newReview);
            if (saved.getMatching().sizeOfReviews() >= 2) {
                saved.getMatching().finish();
            }
            return saved.getReviewId();
        } catch (DataIntegrityViolationException e) {
            throw new MemberNotFoundException(dto.getReviewerId(), e);
        }
    }

    private Matching fetchMatchingAndValidate(ReviewCreationRequestDto dto) throws RuntimeException {
        if (isDuplicateEvaluationPresent(dto.getEvaluations())) {
            throw new InvalidEvaluationsException();
        }

        if (this.reviewRepository.existsByMatchingIdAndReviewerId(dto.getMatchingId(), dto.getReviewerId())) {
            throw new DuplicateReviewException("중복된 리뷰 등록입니다", dto.getMatchingId());
        }

        Matching targetMatching = this.matchingRepository.findById(dto.getMatchingId())
                .orElseThrow(() -> new MatchingNotFoundException(dto.getMatchingId()));

        // 성립되지 않은 매칭에 대해 후기를 남기려 할 경우
        if (targetMatching.getStatus() != MatchingStatus.ACCEPTED) {
            throw new IllegalStateException();
        }

        LocalDateTime now = LocalDateTime.now();
        // 만나기 전에 후기를 남기려 할 경우
        if (now.isBefore(targetMatching.getMeetingTime())) {
            throw new ReviewBeforeMeetingException(now, targetMatching.getMeetingTime());
        }

        return targetMatching;
    }

    /**
     * 특정 회원에게 달린 리뷰 정보를 가져온다.
     *
     * @param revieweeId 리뷰 대상 ID
     * @return revieweeId에 해당하는 회원에게 달린 리뷰 정보가 담긴 DTO
     */
    public RevieweeResponseDto getRevieweeInfo(Long revieweeId) {
        if (!this.memberRepository.existsById(revieweeId)) {
            throw new MemberNotFoundException(revieweeId);
        }

        // TODO: 이 두 개의 쿼리를 어떻게든 하나로 묶는 게 나을까?
        List<RevieweeStatisticsMapping> statistics = this.reviewRepository.getRevieweeStatistics(revieweeId);
        double averageScore = this.reviewRepository.calculateAverageScoreByRevieweeId(revieweeId);

        Map<EvaluationType, Map<Integer, Long>> evaluationDetailCountsByEvaluationType = new HashMap<>();
        for (RevieweeStatisticsMapping mapping : statistics) {
            EvaluationType evaluationType = mapping.getEvaluationType();
            if (!evaluationDetailCountsByEvaluationType.containsKey(evaluationType)) {
                evaluationDetailCountsByEvaluationType.put(evaluationType, new HashMap<>());
            }
            Map<Integer, Long> countByEvaluationId = evaluationDetailCountsByEvaluationType.get(evaluationType);
            countByEvaluationId.put(mapping.getEvaluationDetailId(), mapping.getEvaluationDetailCount());
        }

        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
        for (Map.Entry<EvaluationType, Map<Integer, Long>> entry1 : evaluationDetailCountsByEvaluationType.entrySet()) {
            List<ReviewDetailPerEvaluationType> reviewDetailsPerEvaluationType = new ArrayList<>();
            long totalCountPerEvaluationType = 0L;
            for (Map.Entry<Integer, Long> entry2 : entry1.getValue().entrySet()) {
                reviewDetailsPerEvaluationType.add(new ReviewDetailPerEvaluationType(
                        entry2.getKey(), entry2.getValue()
                ));
                totalCountPerEvaluationType += entry2.getValue();
            }
            reviewResponseDtos.add(
                    new ReviewResponseDto(
                            entry1.getKey(),
                            totalCountPerEvaluationType,
                            reviewDetailsPerEvaluationType
                    )
            );
        }
        return new RevieweeResponseDto(revieweeId, averageScore, reviewResponseDtos);
    }

    private boolean isDuplicateEvaluationPresent(List<ReviewEvaluationDto> evaluations) {
        if (evaluations == null) {
            return true;
        }

        // TODO: iteration 때문에 성능상 약간 좀 안 좋을 수 있음 - 이걸 DB에서 검증하도록 하는 게 나을까?
        Map<EvaluationType, Set<Integer>> evaluationIdsByEvaluationType = new HashMap<>();
        for (ReviewEvaluationDto evaluationDto : evaluations) {
            EvaluationType evaluationType = evaluationDto.getEvaluationType();
            Integer evaluationId = evaluationDto.getEvaluationDetailId();
            if (evaluationIdsByEvaluationType.containsKey(evaluationType)) {
                Set<Integer> ids = evaluationIdsByEvaluationType.get(evaluationType);
                if (ids.contains(evaluationId)) {
                    return true;
                }
                ids.add(evaluationId);
            } else {
                Set<Integer> idSet = new HashSet<>();
                idSet.add(evaluationId);
                evaluationIdsByEvaluationType.put(evaluationType, idSet);
            }
        }
        return false;
    }
}
