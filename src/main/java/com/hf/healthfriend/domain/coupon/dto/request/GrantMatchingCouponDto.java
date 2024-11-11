package com.hf.healthfriend.domain.coupon.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class GrantMatchingCouponDto extends GrantCouponDto {

    @Min(1)
    @Max(5)
    private Integer achievedLevel;

    @Min(1)
    private int grantedMatchingCount;
}
