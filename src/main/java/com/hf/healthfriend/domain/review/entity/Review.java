package com.hf.healthfriend.domain.review.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.review.constants.EvaluationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Member reviewee;

    @Column(name = "matching_id") // TODO Matching 엔티티가 정의되면 JoinColumn으로 변경
    private Long matchingId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_time")
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "evaluation_type", nullable = false)
    private EvaluationType evaluationType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public Review(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Review(Member reviewer,
                  Member reviewee,
                  Long matchingId, // TODO
                  String description,
                  Integer score,
                  EvaluationType evaluationType) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.matchingId = matchingId; // TODO
        this.description = description;
        this.score = score;
        this.evaluationType = evaluationType;
    }

    public Review(Member reviewer,
                  Member reviewee,
                  Long matchingId, // TODO
                  String description,
                  LocalDateTime creationTime,
                  Integer score,
                  EvaluationType evaluationType) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.matchingId = matchingId; // TODO
        this.description = description;
        this.creationTime = creationTime;
        this.score = score;
        this.evaluationType = evaluationType;
    }

    public void updateDescription(String description) {
        this.lastModified = LocalDateTime.now();
        this.description = description;
    }

    public void updateScore(Integer score) {
        this.lastModified = LocalDateTime.now();
        this.score = score;
    }

    public void updateEvaluationType(EvaluationType evaluationType) {
        this.lastModified = LocalDateTime.now();
        this.evaluationType = evaluationType;
    }

    public void delete() {
        this.lastModified = LocalDateTime.now();
        this.isDeleted = true;
    }
}
