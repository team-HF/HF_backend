package com.hf.healthfriend.domain.review.repository;

import com.hf.healthfriend.domain.review.entity.Review;
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
    List<Integer> findScoreListByRevieewId(@Param("revieewId")Long revieewId);
}
