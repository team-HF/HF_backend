package com.hf.healthfriend.domain.coupon.dto.request;

import com.hf.healthfriend.domain.coupon.entity.MatchingCoupon;
import com.hf.healthfriend.domain.member.entity.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class GrantMatchingCouponDto extends GrantCouponDto {

    @Min(1)
    @Max(5)
    private Integer achievedLevel;

    @Min(1)
    private int grantedMatchingCount;

    @Builder
    public GrantMatchingCouponDto(Long receiverId,
                                  long validTimeAmount,
                                  ChronoUnit validTimeUnit,
                                  Integer achievedLevel,
                                  int grantedMatchingCount) {
        super(receiverId, validTimeAmount, validTimeUnit);
        this.achievedLevel = achievedLevel;
        this.grantedMatchingCount = grantedMatchingCount;
    }

    @Override
    public MatchingCoupon toEntity() {
        return new MatchingCoupon(
                new Member(super.getReceiverId()),
                Duration.of(super.getValidTimeAmount(), super.getValidTimeUnit()),
                this.achievedLevel,
                this.grantedMatchingCount
        );
    }
}
