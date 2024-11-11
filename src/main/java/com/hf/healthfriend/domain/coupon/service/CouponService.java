package com.hf.healthfriend.domain.coupon.service;

import com.hf.healthfriend.domain.coupon.dto.request.GrantCouponDto;

public interface CouponService {

    Long grantCoupon(GrantCouponDto dto);
}
