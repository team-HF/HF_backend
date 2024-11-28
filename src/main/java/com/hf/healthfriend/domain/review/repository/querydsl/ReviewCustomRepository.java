package com.hf.healthfriend.domain.review.repository.querydsl;

import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsQueryResultDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewCustomRepository {

    double calculateAverageScoreByRevieweeId(@Param("revieweeId") Long revieweeId);

    boolean existsByMatchingIdAndReviewerId(@Param("matchingId") Long matchingId, @Param("reviewerId") Long reviewerId);

    List<RevieweeStatisticsQueryResultDto> getRevieweeStatistics(Long revieweeId);
}
