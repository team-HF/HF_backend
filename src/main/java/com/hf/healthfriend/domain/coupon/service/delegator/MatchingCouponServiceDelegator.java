package com.hf.healthfriend.domain.coupon.service.delegator;

import com.hf.healthfriend.domain.coupon.dto.request.GrantCouponDto;
import com.hf.healthfriend.domain.coupon.dto.request.GrantMatchingCouponDto;
import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.entity.MatchingCoupon;
import com.hf.healthfriend.domain.coupon.repository.CouponRepository;
import com.hf.healthfriend.domain.coupon.service.CouponServiceDelegator;
import com.hf.healthfriend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingCouponServiceDelegator implements CouponServiceDelegator {
    private static final Set<Class<?>> TYPES_SUPPORTED = Set.of(
            GrantMatchingCouponDto.class
    );

    private final CouponRepository couponRepository;

    @Override
    public Long grantCoupon(GrantCouponDto dto) {
        return grantCoupon((GrantMatchingCouponDto)dto);
    }

    private Long grantCoupon(GrantMatchingCouponDto dto) {
        Coupon coupon = new MatchingCoupon(
                new Member(dto.getReceiverId()),
                Duration.of(dto.getValidTimeAmount(), dto.getValidTimeUnit()),
                dto.getAchievedLevel(),
                dto.getGrantedMatchingCount()
        );
        Coupon saved = this.couponRepository.save(coupon);
        return saved.getCouponId();
    }

    @Override
    public boolean supports(Class<?> dtoClass) {
        return TYPES_SUPPORTED.contains(dtoClass);
    }
}
