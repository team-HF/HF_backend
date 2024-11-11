package com.hf.healthfriend.domain.coupon.repository;

import com.hf.healthfriend.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
