package com.hf.healthfriend.domain.matching.entity;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long matchingId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "beginner_id")
    private Member beginner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "advanced_id")
    private Member advanced;

    @Enumerated(EnumType.STRING)
    private MatchingStatus status = MatchingStatus.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime meetingTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationTime = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime finishTime = null;

    public Matching(Long matchingId) {
        this.matchingId = matchingId;
    }

    public Matching(Member beginner, Member advanced, LocalDateTime meetingTime) {
        this.beginner = beginner;
        beginner.getMatchingsAsBeginner().add(this);
        this.advanced = advanced;
        advanced.getMatchingsAsAdvanced().add(this);
        this.meetingTime = meetingTime;
    }

    public void accept() {
        this.status = MatchingStatus.ACCEPTED;
    }

    public void reject() {
        this.status = MatchingStatus.REJECTED;
    }

    public void finish() {
        this.status = MatchingStatus.FINISHED;
        this.finishTime = LocalDateTime.now();
    }
}
