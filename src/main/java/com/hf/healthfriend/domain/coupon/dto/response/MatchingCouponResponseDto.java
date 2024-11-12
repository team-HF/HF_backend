package com.hf.healthfriend.domain.coupon.dto.response;

import com.hf.healthfriend.domain.coupon.constant.CouponType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class MatchingCouponResponseDto extends CouponResponseDto {
    private final Integer achievedLevel;
    private final int grantedMatchingCount;
    private final int leftMatchingCount;

    @Builder
    public MatchingCouponResponseDto(Long couponId,
                                     Long receiverId,
                                     CouponType couponType,
                                     LocalDateTime expirationTime,
                                     int validPeriodInDays,
                                     LocalDateTime grantTime,
                                     boolean used,
                                     boolean read,
                                     boolean expired,
                                     Integer achievedLevel,
                                     int grantedMatchingCount,
                                     int leftMatchingCount) {
        super(couponId, receiverId, couponType, expirationTime, validPeriodInDays, grantTime, used, read, expired);
        this.achievedLevel = achievedLevel;
        this.grantedMatchingCount = grantedMatchingCount;
        this.leftMatchingCount = leftMatchingCount;
    }
}
