package com.hf.healthfriend.domain.matching.entity;

import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long matchingId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "beginner_id")
    private Member beginner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "advanced_id")
    private Member advanced;

    @Column(name = "is_accepted")
    private boolean isAccepted = false;

    @Column(name = "is_canceled")
    private boolean isCanceled = false;

    public Matching(Long matchingId) {
        this.matchingId = matchingId;
    }

    public Matching(Member beginner, Member advanced) {
        this.beginner = beginner;
        this.advanced = advanced;
    }
}
