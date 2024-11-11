package com.hf.healthfriend.domain.coupon.entity;

import com.hf.healthfriend.domain.coupon.exception.InvalidMatchingCountException;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Period;

@Entity
@DiscriminatorValue("m")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingCoupon extends Coupon {
    private Integer achievedLevel;
    private int grantedMatching;
    private int leftMatchingCount;

    public MatchingCoupon(Member receiver, LocalDateTime expiration, Integer achievedLevel, int grantedMatching) {
        super(receiver, expiration);
        this.achievedLevel = achievedLevel;
        this.grantedMatching = grantedMatching;
        this.leftMatchingCount = grantedMatching;
    }

    public MatchingCoupon(Member receiver, Period validPeriod, Integer achievedLevel, int grantedMatching) {
        super(receiver, validPeriod);
        this.achievedLevel = achievedLevel;
        this.grantedMatching = grantedMatching;
        this.leftMatchingCount = grantedMatching;
    }

    private void checkRepresentationInvariant() {
        if (this.grantedMatching <= 0) {
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
