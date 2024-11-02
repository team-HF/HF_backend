package com.hf.healthfriend.domain.review.repository.querydsl;

import org.springframework.data.repository.query.Param;

public interface ReviewCustomRepository {

    double calculateAverageScoreByRevieweeId(@Param("revieweeId") Long revieweeId);

    boolean existsByMatchingIdAndReviewerId(@Param("matchingId") Long matchingId, @Param("reviewerId") Long reviewerId);
}
