package com.hf.healthfriend.domain.coupon.repository.querydsl;

import com.hf.healthfriend.domain.coupon.constant.CouponFetchType;
import com.hf.healthfriend.domain.coupon.entity.Coupon;

import java.util.List;

public interface CouponCustomRepository {

    List<Coupon> findByReceiverIdAndFetchType(Long receiverId, CouponFetchType couponFetchType);
}
