package com.hf.healthfriend.domain.review.repository;

import com.hf.healthfriend.domain.review.entity.Review;
import com.hf.healthfriend.domain.review.repository.querydsl.ReviewCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
}
