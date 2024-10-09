package com.hf.healthfriend.domain.review.entity;

import com.hf.healthfriend.domain.review.constants.EvaluationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "review_evaluation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class ReviewEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_evaluation_id")
    private Long reviewEvaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(name = "evaluation_type")
    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

    @Column(name = "evaluation_detail_id")
    private Integer evaluationDetailId;

    public ReviewEvaluation(EvaluationType evaluationType,
                            Integer evaluationDetailId) {
        this.evaluationType = evaluationType;
        this.evaluationDetailId = evaluationDetailId;
    }

    public ReviewEvaluation(Review review,
                            EvaluationType evaluationType,
                            Integer evaluationDetailId) {
        this(evaluationType, evaluationDetailId);
        this.review = review;
    }

    void setReview(Review review) { // Review 객체에서만 접근이 가능하도록 default access modifier 설정
        this.review = review;
    }
}
