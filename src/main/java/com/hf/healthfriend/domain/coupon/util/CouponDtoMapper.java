package com.hf.healthfriend.domain.coupon.util;

import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;
import com.hf.healthfriend.domain.coupon.entity.Coupon;

public interface CouponDtoMapper {

    CouponResponseDto mapToCouponResponseDto(Coupon entity);
}
