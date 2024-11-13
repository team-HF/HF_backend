package com.hf.healthfriend.domain.coupon.service;

import com.hf.healthfriend.domain.coupon.constant.CouponFetchType;
import com.hf.healthfriend.domain.coupon.dto.request.GrantCouponDto;
import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;
import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.exception.NoSupportedCouponDelegatorException;
import com.hf.healthfriend.domain.coupon.exception.UnavailableCouponException;
import com.hf.healthfriend.domain.coupon.repository.CouponRepository;
import com.hf.healthfriend.domain.coupon.util.CouponDtoMapper;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

    public void readCoupon(Long couponId) {
        this.couponRepository.setReadById(couponId);
    }

    /**
     * 쿠폰을 사용한다.
     *
     * @param couponId 사용할 쿠폰의 ID
     * @throws UnavailableCouponException 기간이 만료되었거나 이미 사용한 쿠폰인 경우와 같이 사용할 수 없는 쿠폰일 경우
     * @throws NoSuchElementException couponId에 해당하는 쿠폰이 존재하지 않을 경우
     */
    public void useCoupon(Long couponId) throws UnavailableCouponException, NoSuchElementException {
        Coupon coupon = this.couponRepository.findById(couponId)
                .orElseThrow(NoSuchElementException::new);
        if (coupon.isNotAvailable()) {
            throw new UnavailableCouponException(couponId);
        }

        coupon.use();
    }
}
