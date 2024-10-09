package com.hf.healthfriend.domain.review.repository;

import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            SELECT
                re.evaluation_type AS evaluationType,
                re.evaluation_detail_id AS evaluationDetailId,
                COUNT(re.evaluation_detail_id) AS evaluationDetailCount
            FROM review_evaluation re
            INNER JOIN review r ON re.review_id = r.review_id
            INNER JOIN matching m ON r.matching_id = m.matching_id
            WHERE r.is_deleted = FALSE
                AND (m.advanced_id = r.reviewer_id AND m.beginner_id = :revieweeId
                    OR m.beginner_id = r.reviewer_id AND m.advanced_id = :revieweeId)
            GROUP BY re.evaluation_type, re.evaluation_detail_id
            ORDER BY evaluationDetailCount DESC, evaluationType
            """, nativeQuery = true)
    List<RevieweeStatisticsMapping> getRevieweeStatistics(@Param("revieweeId") Long revieweeId);
}
