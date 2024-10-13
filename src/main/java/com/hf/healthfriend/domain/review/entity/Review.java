package com.hf.healthfriend.domain.review.entity;

import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Member reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id")
    private Matching matching;

    @Column(name = "creation_time")
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReviewEvaluation> reviewEvaluations = new ArrayList<>();

    public Review(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Review(Member reviewer,
                  Member reviewee,
                  Matching matching,
                  Integer score,
                  List<ReviewEvaluation> reviewEvaluations) {
        this(reviewer, reviewee, matching, LocalDateTime.now(), score, reviewEvaluations);
    }

    public Review(Member reviewer,
                  Member reviewee,
                  Matching matching,
                  LocalDateTime creationTime,
                  Integer score,
                  List<ReviewEvaluation> reviewEvaluations) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.matching = matching;
        this.creationTime = creationTime;
        this.score = score;
        reviewEvaluations.forEach((re) -> re.setReview(this));
        this.reviewEvaluations = reviewEvaluations;
    }

    public void updateScore(Integer score) {
        this.lastModified = LocalDateTime.now();
        this.score = score;
    }

    public void delete() {
        this.lastModified = LocalDateTime.now();
        this.isDeleted = true;
    }
}
