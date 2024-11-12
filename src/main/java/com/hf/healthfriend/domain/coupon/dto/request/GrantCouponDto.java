package com.hf.healthfriend.domain.coupon.dto.request;

import lombok.*;

import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public abstract class GrantCouponDto {
    private Long receiverId;
    private long validTimeAmount;
    private ChronoUnit validTimeUnit;
}
