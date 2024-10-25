package com.hf.healthfriend.domain.matching.entity;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long matchingId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "requester_id")
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "request_target_id")
    private Member targetMember;

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

    public Matching(Member requester, Member targetMember, LocalDateTime meetingTime) {
        // TODO: Member entity 안에 addMatching 메소드 추가해야 함
        this.requester = requester;
        requester.getMatchingRequests().add(this);
        this.targetMember = targetMember;
        targetMember.getMatchingsReceived().add(this);
        this.meetingTime = meetingTime;
    }

    public void accept() {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("대기 중인 매칭만 수락할 수 있습니다");
        }
        this.status = MatchingStatus.ACCEPTED;
    }

    public void reject() {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("대기 중인 매칭만 거절할 수 있습니다");
        }
        this.status = MatchingStatus.REJECTED;
    }

    public void finish() {
        if (this.status != MatchingStatus.ACCEPTED) {
            throw new IllegalArgumentException("수락된 매칭만 종료될 수 있습니다");
        }
        this.status = MatchingStatus.FINISHED;
        this.finishTime = LocalDateTime.now();
    }
}
