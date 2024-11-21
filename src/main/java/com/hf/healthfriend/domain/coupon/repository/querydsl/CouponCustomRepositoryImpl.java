package com.hf.healthfriend.domain.coupon.repository.querydsl;

import com.hf.healthfriend.domain.coupon.constant.CouponFetchType;
import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.entity.QCoupon;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponCustomRepositoryImpl implements CouponCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QCoupon coupon = QCoupon.coupon;

    @Override
    public List<Coupon> findByReceiverIdAndFetchType(Long receiverId, CouponFetchType couponFetchType) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(this.coupon.receiver.id.eq(receiverId));
        switch (couponFetchType) {
            case USED -> booleanBuilder.and(this.coupon.used.isTrue());
            case AVAILABLE -> {
                booleanBuilder.and(this.coupon.used.isFalse());
                booleanBuilder.and(this.coupon.expiration.after(LocalDateTime.now()));
            }
            case EXPIRED -> {
                booleanBuilder.and(this.coupon.expiration.before(LocalDateTime.now()));
            }
        }

        return this.queryFactory.selectFrom(this.coupon)
                .where(booleanBuilder)
                .orderBy(this.coupon.creationTime.desc())
                .fetch();
    }
}
