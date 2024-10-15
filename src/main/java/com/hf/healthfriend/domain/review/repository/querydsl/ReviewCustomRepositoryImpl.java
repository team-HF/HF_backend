package com.hf.healthfriend.domain.review.repository.querydsl;

import com.hf.healthfriend.domain.review.entity.QReview;
import com.hf.healthfriend.domain.review.entity.QReviewEvaluation;
import com.hf.healthfriend.domain.review.repository.dto.RevieweeStatisticsMapping;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<RevieweeStatisticsMapping> getRevieweeStatistics(Long revieweeId) {
        return this.queryFactory.select(
                        Projections.constructor(RevieweeStatisticsMapping.class,
                                this.reviewEvaluation.evaluationType,
                                this.reviewEvaluation.evaluationDetailId,
                                this.reviewEvaluation.reviewEvaluationId.count()
                                        .as(EVALUATION_DETAIL_COUNT_ALIAS)))
                .from(this.reviewEvaluation)
                .innerJoin(this.review).on(this.reviewEvaluation.review.eq(this.review))
                .where(this.review.reviewee.id.eq(revieweeId))
                .groupBy(this.reviewEvaluation.evaluationType, this.reviewEvaluation.evaluationDetailId)
                .orderBy(EVALUATION_DETAIL_COUNT_ALIAS.desc(), this.orderByEvaluationType)
                .fetch();
    }

    @Override
    public double calculateAverageScoreByRevieweeId(Long revieweeId) {
        Double result = this.queryFactory.select(
                        this.review.score.avg())
                .from(this.review)
                .where(this.review.reviewee.id.eq(revieweeId))
                .fetchOne();
        return result == null ? 0 : result;
    }
}
