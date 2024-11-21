package com.hf.healthfriend.domain.coupon.service;

import com.hf.healthfriend.domain.coupon.constant.CouponFetchType;
import com.hf.healthfriend.domain.coupon.dto.request.GrantCouponDto;
import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;

import java.util.List;

public interface CouponService {

    Long grantCoupon(GrantCouponDto dto);

    List<CouponResponseDto> getCoupons(Long memberId, CouponFetchType fetchType);
}
