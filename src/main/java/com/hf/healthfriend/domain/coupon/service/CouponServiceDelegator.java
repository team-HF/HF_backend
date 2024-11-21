package com.hf.healthfriend.domain.coupon.service;

public interface CouponServiceDelegator extends CouponService {

    boolean supports(Class<?> dtoClass);
}
