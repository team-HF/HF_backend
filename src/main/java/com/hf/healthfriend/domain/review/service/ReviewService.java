package com.hf.healthfriend.domain.review.service;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import com.hf.healthfriend.domain.review.dto.request.ReviewEvaluationDto;
import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.entity.ReviewEvaluation;
import com.hf.healthfriend.domain.review.exception.InvalidEvaluationsException;
import com.hf.healthfriend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MatchingRepository matchingRepository;

    /**
     * 리뷰를 추가한다.
     *
     * @param dto 리뷰 정보가 담긴 DTO
     * @return 자동 생성된 리뷰의 ID
     */
    public Long addReview(ReviewCreationRequestDto dto) {
        if (isDuplicateEvaluationPresent(dto.getEvaluations())) {
            throw new InvalidEvaluationsException();
        }

        // TODO: 동시성 문제 해결해야 함
        // 한 번에 여러 리뷰를 등록하려고 할 경우 동시성 문제 발생
        // 한 회원이 하나의 매칭에 대해 리뷰를 여러 번 남길 수 있을지도? 이 부분은 기획분들께 여쭤 보고 결정
        Matching targetMatching = this.matchingRepository.findById(dto.getMatchingId())
                .orElseThrow(() -> new MatchingNotFoundException(dto.getMatchingId()));
        Review newReview = new Review(
                new Member(dto.getReviewerId()),
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
            return saved.getReviewId();
        } catch (DataIntegrityViolationException e) {
            throw new MemberNotFoundException(dto.getReviewerId(), e);
        }
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
