package com.hf.healthfriend.domain.coupon.entity;

import com.hf.healthfriend.domain.coupon.exception.InvalidMatchingCountException;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("m")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingCoupon extends Coupon {
    private Integer achievedLevel;
    private int grantedMatchingCount;
    private int leftMatchingCount;

    public MatchingCoupon(Member receiver, LocalDateTime expiration, Integer achievedLevel, int grantedMatchingCount) {
        super(receiver, expiration);
        this.achievedLevel = achievedLevel;
        this.grantedMatchingCount = grantedMatchingCount;
        this.leftMatchingCount = grantedMatchingCount;
    }

    public MatchingCoupon(Member receiver, Duration validPeriod, Integer achievedLevel, int grantedMatchingCount) {
        super(receiver, validPeriod);
        this.achievedLevel = achievedLevel;
        this.grantedMatchingCount = grantedMatchingCount;
        this.leftMatchingCount = grantedMatchingCount;
    }

    private void checkRepresentationInvariant() {
        if (this.grantedMatchingCount <= 0) {
            throw new InvalidMatchingCountException("쿠폰의 매칭 회수는 0보다 큰 정수여야 합니다.");
        }
    }

    @Override
    public void use() {
        if (--this.leftMatchingCount == 0) {
            super.use();
        }
    }
}
