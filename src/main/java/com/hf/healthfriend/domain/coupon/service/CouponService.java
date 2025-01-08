package com.hf.healthfriend.domain.coupon.service;

import com.hf.healthfriend.domain.coupon.constant.CouponFetchType;
import com.hf.healthfriend.domain.coupon.dto.request.GrantCouponDto;
import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;
import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.exception.NoSupportedCouponDelegatorException;
import com.hf.healthfriend.domain.coupon.repository.CouponRepository;
import com.hf.healthfriend.domain.coupon.util.CouponDtoMapper;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponDtoMapper couponDtoMapper;
    private final MemberRepository memberRepository;

    public Long grantCoupon(GrantCouponDto dto) {
        Coupon entity = dto.toEntity();
        Coupon saved = this.couponRepository.save(entity);
        return saved.getCouponId();
    }

    public List<CouponResponseDto> getCoupons(Long receiverId, CouponFetchType fetchType) {
        if (!this.memberRepository.existsById(receiverId)) {
            throw new MemberNotFoundException(receiverId);
        }
        List<Coupon> result = this.couponRepository.findByReceiverIdAndFetchType(receiverId, fetchType);
        return result.stream()
                .map(this.couponDtoMapper::mapToCouponResponseDto)
                .toList();
    }
}
