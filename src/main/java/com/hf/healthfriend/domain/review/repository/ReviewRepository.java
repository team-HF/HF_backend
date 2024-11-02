package com.hf.healthfriend.domain.review.repository;

import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import com.hf.healthfriend.domain.review.repository.querydsl.ReviewCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

//    @Query(value = """
//            SELECT COUNT(1) > 0
//            FROM review AS r
//            WHERE r.matching_id = :matchingId
//                AND r.reviewer_id = :reviewerId
//            LIMIT 1
//            """, nativeQuery = true)
//    boolean existsByMatchingIdAndReviewerId(@Param("matchingId") Long matchingId, @Param("reviewerId") Long reviewerId);

    @Query(value = "SELECT r.score FROM Review r WHERE r.reviewee.id = :revieewId")
    List<Integer> findScoreListByRevieewId(@Param("revieewId") Long revieewId);

    // Query DSL에서는 일반적인 방법으로는 FROM 절이나 JOIN 절에 서브쿼리를 사용할 수 없는 듯함...
    // FROM 절에서 서브쿼리를 사용해야 해서 nativeQuery를 사용하도록 변경
    // TODO: 제일 처음 score 평균을 내는 서브쿼리를 사용하는 것이 성능에 어떤 영향을 미치는지 생각해 봐야 함
    @Query(value = """
            SELECT
                (
                    SELECT AVG(score)
                    FROM review
                    WHERE reviewee_id = :revieweeId
                ) AS scoreAverage,
                re.evaluation_type AS evaluationType,
                re.evaluation_detail_id AS evaluationDetailId,
                re.evaluation_detail_count AS evaluationDetailCount
                FROM (
                    SELECT
                        re2.evaluation_type AS evaluation_type,
                        re2.evaluation_detail_id AS evaluation_detail_id,
                        COUNT(re2.review_evaluation_id) AS evaluation_detail_count
                        FROM review_evaluation re2
                        INNER JOIN review r ON r.review_id = re2.review_id
                        WHERE r.reviewee_id = :revieweeId
                        GROUP BY re2.evaluation_type, re2.evaluation_detail_id
                ) re
                ORDER BY re.evaluation_type, re.evaluation_detail_count DESC
            """, nativeQuery = true)
    List<RevieweeStatisticsMapping> getRevieweeStatistics(Long revieweeId);
}
