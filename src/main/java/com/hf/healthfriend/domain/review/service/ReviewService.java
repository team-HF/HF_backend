package com.hf.healthfriend.domain.review.service;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.exception.MatchingNotFoundException;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // TODO: 동시성 문제 해결해야 함
        // 한 번에 여러 리뷰를 등록하려고 할 경우 동시성 문제 발생
        // 한 회원이 하나의 매칭에 대해 리뷰를 여러 번 남길 수 있을지도? 이 부분은 기획분들께 여쭤 보고 결정
        Matching targetMatching = this.matchingRepository.findById(dto.getMatchingId())
                .orElseThrow(() -> new MatchingNotFoundException(dto.getMatchingId()));
        Review newReview = new Review(
                new Member(dto.getReviewerId()),
                targetMatching,
                dto.getDescription(),
                dto.getScore(),
                dto.getEvaluationType()
        );

        try {
            Review saved = this.reviewRepository.save(newReview);
            return saved.getReviewId();
        } catch (DataIntegrityViolationException e) {
            throw new MemberNotFoundException(dto.getReviewerId(), e);
        }
    }
}
