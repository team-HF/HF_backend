package com.hf.healthfriend.domain.coupon.util.delegator;

import com.hf.healthfriend.domain.coupon.constant.CouponType;
import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;
import com.hf.healthfriend.domain.coupon.dto.response.MatchingCouponResponseDto;
import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.entity.MatchingCoupon;
import com.hf.healthfriend.domain.coupon.util.CouponDtoMappingDelegator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MatchingCouponDtoMappingDelegator implements CouponDtoMappingDelegator {

    @Override
    public CouponResponseDto mapToCouponResponseDto(Coupon entity) {
        return mapToMatchingCouponResponseDto((MatchingCoupon)entity);
    }

    private MatchingCouponResponseDto mapToMatchingCouponResponseDto(MatchingCoupon matchingCoupon) {
        return MatchingCouponResponseDto.builder()
                .couponId(matchingCoupon.getCouponId())
                .receiverId(matchingCoupon.getReceiver().getId())
                .couponType(CouponType.MATCHING_COUPON)
                .expirationTime(matchingCoupon.getExpiration())
                .validPeriodInDays(matchingCoupon.getValidPeriodInDays())
                .grantTime(matchingCoupon.getCreationTime())
                .used(matchingCoupon.isUsed())
                .read(matchingCoupon.isRead())
                .expired(matchingCoupon.isExpired())
                .achievedLevel(matchingCoupon.getAchievedLevel())
                .grantedMatchingCount(matchingCoupon.getGrantedMatchingCount())
                .leftMatchingCount(matchingCoupon.getLeftMatchingCount())
                .build();
    }

    @Override
    public List<Class<?>> getSupportingTypes() {
        return List.of(MatchingCouponResponseDto.class);
    }
}
