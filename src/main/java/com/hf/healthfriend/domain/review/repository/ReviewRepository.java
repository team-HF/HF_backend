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
                re.evaluation_type AS evaluationtype,
                re.evaluation_detail_id AS evaluationdetailid,
                COUNT(re.evaluation_detail_id) AS evaluationdetailcount
            FROM review_evaluation re
            INNER JOIN review r ON re.review_id = r.review_id
            INNER JOIN matching m ON r.matching_id = m.matching_id
            WHERE r.is_deleted = FALSE -- TODO: 리뷰 삭제 기능이 없으므로 제거해야 함
                AND (m.advanced_id = r.reviewer_id AND m.beginner_id = :revieweeId
                    OR m.beginner_id = r.reviewer_id AND m.advanced_id = :revieweeId)
            GROUP BY re.evaluation_type, re.evaluation_detail_id
            ORDER BY evaluationdetailcount DESC, evaluationtype
            """, nativeQuery = true)
    List<RevieweeStatisticsMapping> getRevieweeStatistics(@Param("revieweeId") Long revieweeId);

    @Query(value = """
            SELECT AVG(r.score)
            FROM review r
            INNER JOIN matching m ON r.matching_id = m.matching_id
            WHERE m.advanced_id = r.reviewer_id AND m.beginner_id = :revieweeId
                OR m.beginner_id = r.reviewer_id AND m.advanced_id = :revieweeId
            """, nativeQuery = true)
    double calculateAverageScoreByRevieweeId(@Param("revieweeId") Long revieweeId);
}
