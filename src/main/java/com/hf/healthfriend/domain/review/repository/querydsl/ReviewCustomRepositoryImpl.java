package com.hf.healthfriend.domain.review.repository.querydsl;

import com.hf.healthfriend.domain.review.entity.QReview;
import com.hf.healthfriend.domain.review.entity.QReviewEvaluation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private static final NumberPath<Long> EVALUATION_DETAIL_COUNT_ALIAS =
            Expressions.numberPath(Long.class, "evaluation_detail_count");

    private final JPAQueryFactory queryFactory;
    private final QReviewEvaluation reviewEvaluation = QReviewEvaluation.reviewEvaluation;
    private final QReview review = QReview.review;

    private final OrderSpecifier<?> orderByEvaluationType =
            new OrderSpecifier<>(Order.ASC, this.reviewEvaluation.evaluationType);

    @Override
    public double calculateAverageScoreByRevieweeId(Long revieweeId) {
        Double result = this.queryFactory.select(
                        this.review.score.avg())
                .from(this.review)
                .where(this.review.reviewee.id.eq(revieweeId))
                .fetchOne();
        return result == null ? 0 : result;
    }

    @Override
    public boolean existsByMatchingIdAndReviewerId(Long matchingId, Long reviewerId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(this.review.matching.matchingId.eq(matchingId));
        builder.and(this.review.reviewer.id.eq(reviewerId));

        Integer fetchOne = this.queryFactory
                .selectOne()
                .from(this.review)
                .where(builder)
                .fetchFirst();
        return fetchOne != null;
    }
}
