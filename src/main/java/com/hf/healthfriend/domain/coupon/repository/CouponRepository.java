package com.hf.healthfriend.domain.coupon.repository;

import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.repository.querydsl.CouponCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponCustomRepository {

    @Modifying
    @Query(value = "UPDATE coupon SET is_read = TRUE WHERE coupon_id = :couponId", nativeQuery = true)
    void setReadById(@Param("couponId") Long couponId);
}
