package com.hf.healthfriend.domain.review.repository.querydsl;

import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewCustomRepository {

    List<RevieweeStatisticsMapping> getRevieweeStatistics(@Param("revieweeId") Long revieweeId);

    double calculateAverageScoreByRevieweeId(@Param("revieweeId") Long revieweeId);
}
