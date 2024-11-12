package com.hf.healthfriend.domain.coupon.dto.response;

import com.hf.healthfriend.domain.coupon.constant.CouponType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public abstract class CouponResponseDto {
    private final Long couponId;
    private final Long receiverId;
    private final CouponType couponType;
    private final LocalDateTime expirationTime;
    private final int validPeriodInDays;
    private final LocalDateTime grantTime;
    private final boolean used;
    private final boolean read;
    private final boolean expired;
}
